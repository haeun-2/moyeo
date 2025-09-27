package com.d108.moyeo.presentation.ui.screen.currency.exchange

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.core.BoxStore
import com.d108.moyeo.data.local.UserDataManager
import com.d108.moyeo.domain.model.box.Balance
import com.d108.moyeo.domain.usecase.box.GetPersonalBoxUseCase
import com.d108.moyeo.domain.usecase.exchange.ExchangeUseCase
import com.d108.moyeo.domain.usecase.exchange.ReservationExchangeUseCase
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

    sealed class ExchangeNavEvent {
        data object ShowBiometricPrompt : ExchangeNavEvent()
    }
    private val _navigationEvent = MutableSharedFlow<ExchangeNavEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    // 임시 Correct PIN
    private val correctPin = "111111"
    private var isSubmitting = false

    fun initIfNeeded() {
        val modeArg = (savedStateHandle.get<String>("mode") ?: "CHARGE").uppercase()
        val initialMode =
            runCatching { ExchangeMode.valueOf(modeArg) }.getOrElse { ExchangeMode.CHARGE }
        val targetCurrency =
            savedStateHandle.get<String>("targetCurrency") // CurrencyScreen에서 전달된 통화

        if (_uiState.value.mode != initialMode || _uiState.value == ExchangeUiState(mode = ExchangeMode.CHARGE)) {
            val startStep = when (initialMode) {
                ExchangeMode.CHARGE -> ExchangeStep.TARGET_BOX
                ExchangeMode.REFUND -> ExchangeStep.HOW_MUCH
            }
            _uiState.update { it.copy(mode = initialMode, step = startStep) }

            viewModelScope.launch {
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

                userDataManager.biometricsPreferenceFlow.collect { enabled ->
                    _uiState.update { it.copy(biometricsEnabled = enabled) }
                }
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
        _uiState.update { it.copy(spendCurrencyCode = code, spendCurrencyName = name) }
    }

    fun changeAmount(newAmount: String) {
        _uiState.update { it.copy(amount = newAmount) }
    }

    /** 금액 입력(자릿수/제한 처리: Transfer와 동일 로직) */
    fun onMoneyDigitInput(digit: String) {
        val current = _uiState.value.amount
        val newStr = if (current == "0" && digit != "00") digit else current + digit
        if (current.isEmpty() && digit == "00") return
        if (newStr.length > 10) return

        // 내 잔액과 비교 (선택 통화 기준). REFUND은 KRW 고정.
        val currency = _uiState.value.spendCurrencyCode
        val balance = myPersonalBalances.find { it.currency == currency }?.balance ?: 0.0
        val maxAmount = balance.toLong()
        val newLong = newStr.toLongOrNull() ?: 0L
        if (newLong > maxAmount) {
            _uiState.update { it.copy(amount = maxAmount.toString()) }
            return
        }
        _uiState.update { it.copy(amount = newStr) }
    }

    fun onMoneyBackspace() {
        val c = _uiState.value.amount
        _uiState.update { it.copy(amount = if (c.isNotEmpty()) c.dropLast(1) else "") }
    }

    fun onBiometricsSucceeded() {
        submitExchange()
    }

    fun skipBiometrics() {
        _uiState.update { it.copy(step = ExchangeStep.PIN) }
    }

    fun onPinInput(digit: String) {
        if (_uiState.value.pin.length < 6) {
            _uiState.update { it.copy(pin = it.pin + digit) }
        }
    }
    fun onPinBackspace() { _uiState.update { it.copy(pin = it.pin.dropLast(1)) } }
    fun onPinClear() { _uiState.update { it.copy(pin = "") } }

    private fun checkPin() {
        viewModelScope.launch {
            val s = _uiState.value
            if (s.pin == correctPin) {
                _uiState.update { it.copy(pinFailureCount = 0, pinError = null) }
                onPinSucceeded()
            } else {
                val n = s.pinFailureCount + 1
                if (n >= 3) {
                    _uiState.update { it.copy(isPinLocked = true, pinError = "PIN 3회 오류로 잠겼습니다.", pin = "") }
                } else {
                    _uiState.update { it.copy(pinFailureCount = n, pinError = "PIN이 일치하지 않습니다. (남은 횟수: ${3 - n}회)") }
                    kotlinx.coroutines.delay(1000L)
                    onPinClear()
                }
            }
        }
    }

    fun onPinSucceeded() {
        submitExchange()
    }

    /** 다음 버튼 공통 분기 (Transfer onNextClicked와 동일 구조) */
    fun onNextClicked() {
        when (_uiState.value.step) {
            ExchangeStep.TARGET_BOX -> _uiState.update { it.copy(step = ExchangeStep.CHOOSE_CURRENCY) }
            ExchangeStep.CHOOSE_CURRENCY -> _uiState.update { it.copy(step = ExchangeStep.HOW_MUCH) }
            ExchangeStep.HOW_MUCH -> {
                viewModelScope.launch {
                    if (_uiState.value.biometricsEnabled) {
                        _uiState.update { it.copy(step = ExchangeStep.BIOMETRIC) }
                        _navigationEvent.emit(ExchangeNavEvent.ShowBiometricPrompt)
                    } else {
                        _uiState.update { it.copy(step = ExchangeStep.PIN) }
                    }
                }
            }
            ExchangeStep.BIOMETRIC -> {
                // 버튼으로 넘어온 경우: 생체 스킵 → PIN
                skipBiometrics()
            }
            ExchangeStep.PIN -> {
                if (!_uiState.value.isPinLocked) checkPin()
            }
            ExchangeStep.FINISH -> { /* Screen에서 pop */ }
        }
    }

    private fun submitExchange() {
        if (isSubmitting) return
        val state = _uiState.value
        val amount = state.amount.toLongOrNull() ?: 0L
        if (amount <= 0L) return

        val targetCurrency = savedStateHandle.get<String>("targetCurrency") ?: ""
        val (fromBoxId, fromCurrency, toCurrency) =
            if (state.mode == ExchangeMode.CHARGE) {
                Triple(state.selectedBoxId, state.spendCurrencyCode, targetCurrency)
            } else {
                Triple(myPersonalBoxId ?: -1L, targetCurrency, "KRW")
            }
        if (fromBoxId <= 0L || fromCurrency.isBlank() || toCurrency.isBlank()) return

        isSubmitting = true
        viewModelScope.launch {
            exchangeUseCase(fromBoxId, fromCurrency, toCurrency, amount)
                .onSuccess { _uiState.update { it.copy(step = ExchangeStep.FINISH) } }
                .onFailure { /* TODO: 에러 상태 반영 */ }
            isSubmitting = false
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
}
