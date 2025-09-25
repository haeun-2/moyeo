package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.data.remote.dto.exchange.ExchangeRateItem
import com.d108.moyeo.domain.usecase.exchange.GetExchangeRatesUseCase
import com.d108.moyeo.util.CurrencyUtils.getCurrencyFlag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExchangeKeypadUiState(
    val inputAmount: String = "0", // 입력하는 공간
    val convertedKrwAmount: String = "0", // 입력한값에 krw로 계산 된 값
    val mode: String = "charge",
    val currencyCode: String? = null,
    val currencyName: String? = null,
    val currentRate: Double = 0.0,
    val isLoading: Boolean = true,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ExchangeKeypadViewModel @Inject constructor(
    private val getExchangeRatesUseCase: GetExchangeRatesUseCase
): ViewModel() {

    // API로부터 받아온 전체 환율 정보를 저장하는 변수
    private var allExchangeRates: Map<String, ExchangeRateItem> = emptyMap()

    private val _uiState = MutableStateFlow(ExchangeKeypadUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAllExchangeRates()
    }

    /**
     * 💡 [추가] ViewModel 초기화 시 전체 환율 정보를 미리 로드합니다.
     */
    private fun loadAllExchangeRates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getExchangeRatesUseCase()
                .onSuccess { ratesMap ->
                    allExchangeRates = ratesMap
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "환율 정보를 불러오는데 실패했습니다: ${exception.message}"
                        )
                    }
                }
        }
    }

    fun setModeAndCurrency(mode: String, currencyCode: String?, currencyName: String?) {
        if (allExchangeRates.isEmpty()) {
            viewModelScope.launch {
                delay(100)
                setModeAndCurrency(mode, currencyCode, currencyName)
            }
            return
        }

        val rateItem = allExchangeRates[currencyCode]
        if (rateItem == null) {
            _uiState.update { it.copy(errorMessage = "지원하지 않는 통화입니다.") }
            return
        }

        // 모드에 따라 적용할 환율 결정
        // reservation 모드는 charge와 동일하게 buyRate 사용
        val applicableRate = if (mode == "refund") {
            rateItem.sellRate
        } else {
            rateItem.buyRate
        }

        _uiState.update {
            it.copy(
                mode = mode,
                currencyCode = currencyCode,
                currencyName = currencyName,
                currentRate = applicableRate,
                inputAmount = "0", // 통화 변경 시 입력값 초기화
                convertedKrwAmount = "0"
            )
        }
    }


    // 숫자 입력 처리
    fun onDigitInput(digit: String) {
        val currentAmount = _uiState.value.inputAmount

        var newAmount = currentAmount
        if (currentAmount == "0" && digit != "00") {
            newAmount = digit
        } else if (currentAmount.isEmpty() && digit == "00") {
            return
        } else if (currentAmount == "0" && digit == "00") {
            return
        } else if ((currentAmount + digit).length > 10) {
            return
        } else {
            newAmount = currentAmount + digit
        }

        // 환율 계산
        val convertedKrw = calculateKrwAmount(newAmount)

        _uiState.update {
            it.copy(
                inputAmount = newAmount,
                convertedKrwAmount = convertedKrw
            )
        }
    }


    // 백스페이스 처리
    fun onBackspace() {
        val currentAmount = _uiState.value.inputAmount
        val newAmount = if (currentAmount.length <= 1) "0" else currentAmount.dropLast(1)

        // 환율 계산
        val convertedKrw = calculateKrwAmount(newAmount)

        _uiState.update {
            it.copy(
                inputAmount = newAmount,
                convertedKrwAmount = convertedKrw
            )
        }
    }

    // 환율 계산 함수
    private fun calculateKrwAmount(foreignAmount: String): String {
        if (foreignAmount.isEmpty() || foreignAmount == "0") return "0"
        return try {
            val amount = foreignAmount.toDouble()
            // UiState에 저장된 실제 환율을 사용
            val krwAmount = (amount * _uiState.value.currentRate).toLong()
            java.text.NumberFormat.getNumberInstance().format(krwAmount) // 콤마 추가
        } catch (e: NumberFormatException) {
            "0"
        }
    }

    fun getDisplayExchangeRate(): String {
        val state = _uiState.value
        if (state.currencyCode == null) return ""
        // 보기 좋은 형식으로 환율 정보 표시  // TODO 일화면 100이 기준임. 즉 1달러 = 1300원, 100엔 = 980원 이렇게 표시됨
        return "1 ${state.currencyCode} = ${String.format("%.2f", state.currentRate)} KRW"
    }

    // 통화 단위 가져오기

    // 모드별 화면 제목
    fun getScreenTitle(): String = when (_uiState.value.mode) {
        "refund" -> "돌려받기"
        "reservation" -> "예약하기"
        else -> "충전하기"
    }

    // 모드별 액션 텍스트
    fun getActionText(): String {
        return when (_uiState.value.mode) {
            "charge" -> "충전할"
            "refund" -> "돌려받을"
            "reservation" -> "예약할"
            else -> "충전할"
        }
    }

    // 모드별 잔액 텍스트
    fun getBalanceText(): String {
        return when (_uiState.value.mode) {
            "charge" -> "보유 웨이 머니: 10,000 원 (초과하는 지불 충전)"
            "refund" -> "보유 웨이 머니: 1,000 원"
            "reservation" -> "보유 웨이 머니로 10,000 원 (초과하는 지불 충전)"
            else -> "보유 웨이 머니: 10,000 원 (초과하는 지불 충전)"
        }
    }

    // 통화 정보

    // 환율 히스토리 버튼 표시 여부
    fun shouldShowHistoryButton(): Boolean {
        return _uiState.value.mode != "reservation"
    }

    fun executeTransaction() {
        val currentState = _uiState.value

        if (currentState.inputAmount == "0") {
            _uiState.update { it.copy(errorMessage = "금액을 입력해주세요") }
            return
        }

        _uiState.update { it.copy(isProcessing = true) }

        // 임시 처리 완료
        _uiState.update {
            it.copy(
                isProcessing = false,
                inputAmount = "0"
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}