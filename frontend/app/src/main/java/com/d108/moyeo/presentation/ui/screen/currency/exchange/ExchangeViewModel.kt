package com.d108.moyeo.presentation.ui.screen.currency.exchange

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.core.BoxStore
import com.d108.moyeo.data.local.UserDataManager
import com.d108.moyeo.domain.model.box.Balance
import com.d108.moyeo.domain.model.exchange.Currency
import com.d108.moyeo.domain.usecase.box.GetPersonalBoxUseCase
import com.d108.moyeo.domain.usecase.exchange.ExchangeUseCase
import com.d108.moyeo.domain.usecase.exchange.GetCurrenciesUseCase
import com.d108.moyeo.domain.usecase.exchange.ReservationExchangeUseCase
import com.d108.moyeo.presentation.ui.component.KeypadKey
import com.d108.moyeo.util.CurrencyUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val exchangeUseCase: ExchangeUseCase,
    private val reservationExchangeUseCase: ReservationExchangeUseCase,
    private val getPersonalBox: GetPersonalBoxUseCase,
    private val boxStore: BoxStore,
    private val userDataManager: UserDataManager,
    private val getCurrenciesUseCase: GetCurrenciesUseCase
) : ViewModel() {

    // Transfer와 동일: 화면에서 구독해 사용할 수 있도록 노출
    val boxUiStates = boxStore.boxUiStates               // 박스 카드(색/텍스트 포함) 목록
    val currencies = boxStore.personalCurrencies         // 내 지갑 보유 통화 목록(List<CurrencyData> 형태)

    private val _uiState = MutableStateFlow(
        ExchangeUiState(mode = ExchangeMode.CHARGE)
    )
    val uiState: StateFlow<ExchangeUiState> = _uiState.asStateFlow()

    // 내 개인 박스 및 잔액(잔액 체크/표기용)
    private var myPersonalBoxId: Long? = null
    private var myPersonalBalances: List<Balance> = emptyList()

    private var exchangeRatesMap: Map<String, Currency> = emptyMap()

    fun initIfNeeded() {
        val modeArg = (savedStateHandle.get<String>("mode") ?: "CHARGE").uppercase()
        val initialMode = runCatching { ExchangeMode.valueOf(modeArg) }.getOrElse { ExchangeMode.CHARGE }
        val targetCurrency = savedStateHandle.get<String>("targetCurrency") // CurrencyScreen에서 전달된 통화

        if (_uiState.value.mode != initialMode || _uiState.value == ExchangeUiState(mode = ExchangeMode.CHARGE)) {
            val startStep = when (initialMode) {
                ExchangeMode.CHARGE -> ExchangeStep.TARGET_BOX
                ExchangeMode.REFUND -> ExchangeStep.HOW_MUCH
            }
            _uiState.update { it.copy(
                mode = initialMode,
                step = startStep,
                targetCurrencyCode = targetCurrency ?: "",
                targetCurrencyName = CurrencyUtils.getCurrencyName(targetCurrency ?: "")
            )}

            viewModelScope.launch {
                getCurrenciesUseCase().onSuccess { rates ->
                    // 성공 시, ViewModel 내부 변수에 Map 형태로 저장합니다.
                    exchangeRatesMap = rates
                }.onFailure {
                    // TODO: 환율 정보 로딩 실패 시 에러 처리
                    Log.e("ExchangeViewModel", "Failed to load exchange rates: $it")
                }
                getPersonalBox()
                    .onSuccess { box ->
                        myPersonalBoxId = box.id
                        myPersonalBalances = box.balances

                        if (initialMode == ExchangeMode.REFUND) {
                            val spend = targetCurrency ?: ""
                            val name = currencies.value.find { it.code == spend }?.name ?: ""
                            _uiState.update {
                                it.copy(
                                    selectedBoxId = box.id,          // 내 개인 박스
                                    spendCurrencyCode = spend,       // 선택 외화 (출금)
                                    spendCurrencyName = name
                                )
                            }
                        }
                    }
                    .onFailure { /* TODO: 에러 처리 */ }
            }
        }
    }

    /** 박스 선택 (CHARGE에서만) */
    fun onTargetBoxSelected(boxId: Long) {
        _uiState.update { it.copy(selectedBoxId = boxId) }
    }

    // 지출 통화 선택 (CHARGE 용)
    fun onCurrencySelected(code: String) {
        val name = currencies.value.find { it.code == code }?.name ?: ""

        // 1. 현재 선택된 박스를 찾습니다. (boxUiStates는 BoxStore가 제공하는 실시간 박스 목록)
        val selectedBox = boxUiStates.value.find { it.id == _uiState.value.selectedBoxId }

        // 2. 그 박스의 잔액 목록(balances)에서 방금 선택한 통화(code)의 잔액을 찾습니다.
        val selectedBalance = selectedBox?.balances?.find { it.currency == code }?.balance ?: 0.0

        // 3. UiState를 업데이트하며 spendCurrency 정보와 함께 '찾아낸 잔액'도 저장합니다.
        _uiState.update {
            it.copy(
                spendCurrencyCode = code,
                spendCurrencyName = name,
                availableSpendBalance = selectedBalance
            )
        }}

    fun changeAmount(newAmount: String) {
        _uiState.update { it.copy(amount = newAmount) }
        updateCalculatedValues()
    }

    /** 금액 입력(자릿수/제한 처리: Transfer와 동일 로직) */
    fun onMoneyDigitInput(digit: String) {
        val current = _uiState.value.amount
        val newStr = if (current == "0" && digit != "00") digit else current + digit
        if (current.isEmpty() && digit == "00") return
        if (newStr.length > 10) return

        changeAmount(newStr)
    }

    fun onMoneyBackspace() {
        val c = _uiState.value.amount
        val next = if (c.isNotEmpty()) c.dropLast(1).ifEmpty { "0" } else "0"
        changeAmount(next)
    }

    /** 다음 버튼 공통 분기 (Transfer onNextClicked와 동일 구조) */
    fun onNextClicked() {
        when (_uiState.value.step) {
            ExchangeStep.TARGET_BOX -> {
                _uiState.update { it.copy(step = ExchangeStep.CHOOSE_CURRENCY) }
            }
            ExchangeStep.CHOOSE_CURRENCY -> {
                _uiState.update { it.copy(step = ExchangeStep.HOW_MUCH) }
                updateCalculatedValues()
            }
            ExchangeStep.HOW_MUCH -> {
                submitExchange()
            }
            ExchangeStep.FINISH -> {
                // Screen 에서 pop
            }
        }
    }

    private fun submitExchange() {
        val state = _uiState.value
        val amount = state.amount.toLongOrNull() ?: 0L
        if (amount <= 0L) return

        val targetCurrency = savedStateHandle.get<String>("targetCurrency") ?: ""

        val (fromBoxId, fromCurrency, toCurrency) =
            if (state.mode == ExchangeMode.CHARGE) {
                Triple(
                    state.selectedBoxId,                  // 선택 박스
                    state.spendCurrencyCode,              // 지출 통화
                    targetCurrency                        // 입금 통화 (CurrencyScreen 선택)
                )
            } else {
                Triple(
                    myPersonalBoxId ?: -1L,              // 내 개인 박스
                    targetCurrency,                       // 지출 통화 = 선택 외화
                    "KRW"                                 // 입금 통화 고정
                )
            }

        if (fromBoxId <= 0L || fromCurrency.isBlank() || toCurrency.isBlank()) return

        viewModelScope.launch {
            exchangeUseCase(fromBoxId, fromCurrency, toCurrency, amount)
                .onSuccess { _uiState.update { it.copy(step = ExchangeStep.FINISH) } }
                .onFailure { /* TODO: 에러 메시지 상태 보관/노출 */ }
        }
    }

    /** 뒤로가기 분기 (Transfer onBackClick 동일 패턴) */
    fun onBackClick() {
        val current = _uiState.value.step
        val root = if (_uiState.value.mode == ExchangeMode.CHARGE) ExchangeStep.TARGET_BOX else ExchangeStep.HOW_MUCH
        if (current == root || current == ExchangeStep.FINISH) {
            // Screen 쪽에서 popBackStack
        } else {
            val prev = when (current) {
                ExchangeStep.CHOOSE_CURRENCY -> if (_uiState.value.mode == ExchangeMode.CHARGE) ExchangeStep.TARGET_BOX else root
                ExchangeStep.HOW_MUCH       -> if (_uiState.value.mode == ExchangeMode.CHARGE) ExchangeStep.CHOOSE_CURRENCY else root
                else                        -> current
            }
            _uiState.update { it.copy(step = prev) }
        }
    }

    /** 잔액 텍스트 포맷터 (필요 시 화면에서 사용할 수 있게 노출) */
    fun myBalanceText(): String {
        val c = _uiState.value.spendCurrencyCode
        val bal = myPersonalBalances.find { it.currency == c }?.balance ?: 0.0
        val df = DecimalFormat("#,##0.####")
        return "잔액: ${df.format(bal)} $c"
    }

    fun onKeyPress(key: KeypadKey) {
        val currentAmount = _uiState.value.amount
        var newAmount = currentAmount

        when (key) {
            // 1. 숫자 키가 눌렸을 때
            is KeypadKey.Digit -> {
                newAmount = if (currentAmount == "0") key.value.toString() else currentAmount + key.value
            }
            // 2. 초기화(Clear) 키가 눌렸을 때 ('00' 모드)
            is KeypadKey.Clear -> {
                newAmount = if (currentAmount == "0") "0" else currentAmount + "00"
            }
            // 3. 백스페이스 키가 눌렸을 때
            is KeypadKey.Backspace -> {
                newAmount = if (currentAmount.length > 1) currentAmount.dropLast(1) else "0"
            }
            // 4. 커스텀 키 (현재 시나리오에서는 사용되지 않음)
            is KeypadKey.Custom -> {
                // 필요 시 로직 추가
            }
        }

        // 자릿수 제한
        if (newAmount.length > 10) {
            return
        }

        // 모든 키 입력의 최종 결과로 changeAmount를 호출하여 환율 계산 실행
        changeAmount(newAmount)
    }

    private fun updateCalculatedValues() {
        val state = _uiState.value
        val amountInTarget = state.amount.toDoubleOrNull() ?: 0.0

        val toCurrency = state.targetCurrencyCode
        val fromCurrency = state.spendCurrencyCode

        // 이 부분은 기존 changeAmount 함수의 계산 로직과 완전히 동일합니다.
        if (fromCurrency != "KRW" && toCurrency != "KRW") { // 외화 -> 외화
            val rateInfoForBuy = exchangeRatesMap[toCurrency]
            var rateStep2 = rateInfoForBuy?.buyRate?.toDouble() ?: 0.0
            val rateInfoForSell = exchangeRatesMap[fromCurrency]
            var rateStep1 = rateInfoForSell?.sellRate?.toDouble() ?: 0.0

            if (toCurrency == "JPY") {
                rateStep2 /= 100.0
            }
            // ✨ 만약 지출 통화(fromCurrency)가 엔화이면, 똑같이 100으로 나눠서 1엔당 가격으로 변환
            if (fromCurrency == "JPY") {
                rateStep1 /= 100.0
            }

            val requiredKrw = amountInTarget * rateStep2
            val requiredSpendAmount = if (rateStep1 > 0) requiredKrw / rateStep1 else 0.0
            _uiState.update {
                it.copy(
                    requiredSpendAmount = requiredSpendAmount,
                    isMultiStepExchange = true,
                    rateForStep1 = rateStep1,
                    rateForStep2 = rateStep2
                )
            }
        } else { // 원화 -> 외화
            val rateInfo = exchangeRatesMap[toCurrency]
            var currentRate = rateInfo?.buyRate?.toDouble() ?: 0.0

            if (toCurrency == "JPY") {
                currentRate /= 100.0
            }

            val requiredSpendAmount = amountInTarget * currentRate
            _uiState.update {
                it.copy(
                    requiredSpendAmount = requiredSpendAmount,
                    isMultiStepExchange = false,
                    rateForStep1 = currentRate,
                    rateForStep2 = 0.0
                )
            }
        }
    }
}
