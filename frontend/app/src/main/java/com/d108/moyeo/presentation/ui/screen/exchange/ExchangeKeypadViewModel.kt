package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ExchangeKeypadUiState(
    val inputAmount: String = "", // 입력하는 공간
    val convertedKrwAmount: String = "0", // 입력한값에 krw로 계산 된 값
    val mode: String = "charge",
    val currencyCode: String? = null,
    val currencyName: String? = null,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null
)

class ExchangeKeypadViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ExchangeKeypadUiState())
    val uiState = _uiState.asStateFlow()

    fun setMode(mode: String) {
        _uiState.update { it.copy(mode = mode) }
    }

    fun setReservationMode(currencyCode: String, currencyName: String) {
        _uiState.update {
            it.copy(
                mode = "reservation",
                currencyCode = currencyCode,
                currencyName = currencyName
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
        if (foreignAmount == "0" || foreignAmount.isEmpty()) return "0"

        try {
            val amount = foreignAmount.toDouble()
            val exchangeRate = getExchangeRate()
            val krwAmount = (amount * exchangeRate).toInt()
            return krwAmount.toString()
        } catch (e: NumberFormatException) {
            return "0"
        }
    }

    // 현재 통화의 환율 가져오기 (1 외화 = ? 원)
    private fun getExchangeRate(): Double {
        val currencyCode = if (_uiState.value.mode == "reservation" && _uiState.value.currencyCode != null) {
            _uiState.value.currencyCode!!
        } else {
            "JPY" // 기본값
        }

        return when (currencyCode) {
            "USD" -> 1340.0
            "EUR" -> 1450.0
            "JPY" -> 9.3942  // 100엔 = 939.42원 → 1엔 = 9.3942원
            "GBP" -> 1650.0
            "CNY" -> 185.0
            "CAD" -> 980.0
            "AUD" -> 880.0
            "CHF" -> 1480.0
            "HKD" -> 170.0
            "SGD" -> 980.0
            "SEK" -> 145.0
            "NOK" -> 130.0
            "NZD" -> 820.0
            "THB" -> 38.0
            "VND" -> 0.055  // 1 VND = 0.055원
            "IDR" -> 0.09   // 1 IDR = 0.09원
            "MYR" -> 300.0
            "PHP" -> 24.0
            "INR" -> 16.0
            "TWD" -> 42.0
            "BRL" -> 270.0
            "MXN" -> 78.0
            "ZAR" -> 75.0
            "TRY" -> 40.0
            "RUB" -> 15.0
            else -> 9.3942 // 기본값 (JPY)
        }
    }

    // 통화 단위 가져오기
    fun getCurrencyUnit(): String {
        val currencyCode = if (_uiState.value.mode == "reservation" && _uiState.value.currencyCode != null) {
            _uiState.value.currencyCode!!
        } else {
            "JPY"
        }

        return when (currencyCode) {
            "USD" -> "달러"
            "EUR" -> "유로"
            "JPY" -> "엔"
            "GBP" -> "파운드"
            "CNY" -> "위안"
            "CAD" -> "달러"
            "AUD" -> "달러"
            "CHF" -> "프랑"
            "HKD" -> "달러"
            "SGD" -> "달러"
            "SEK" -> "크로나"
            "NOK" -> "크로네"
            "NZD" -> "달러"
            "THB" -> "바트"
            "VND" -> "동"
            "IDR" -> "루피아"
            "MYR" -> "링깃"
            "PHP" -> "페소"
            "INR" -> "루피"
            "TWD" -> "달러"
            "BRL" -> "헤알"
            "MXN" -> "페소"
            "ZAR" -> "랜드"
            "TRY" -> "리라"
            "RUB" -> "루블"
            else -> "엔"
        }
    }

    // 모드별 화면 제목
    fun getScreenTitle(): String {
        return when (_uiState.value.mode) {
            "charge" -> "충전하기"
            "refund" -> "돌려받기"
            "reservation" -> "예약하기"
            else -> "충전하기"
        }
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
    fun getDisplayCurrencyName(): String {
        return if (_uiState.value.mode == "reservation" && _uiState.value.currencyName != null) {
            _uiState.value.currencyName!!
        } else {
            "일본 JPY"
        }
    }
    fun getDisplayCurrencyCode(): String {
        return if (_uiState.value.mode == "reservation" && _uiState.value.currencyCode != null) {
            _uiState.value.currencyCode!!
        } else {
            "JPY"
        }
    }

    fun getDisplayExchangeRate(): String {
        return if (_uiState.value.mode == "reservation" && _uiState.value.currencyCode != null) {
            getExchangeRateText(_uiState.value.currencyCode!!)
        } else {
            "100 엔 = 980 원"
        }
    }

    fun getDisplayFlag(): String {
        return if (_uiState.value.mode == "reservation" && _uiState.value.currencyCode != null) {
            getCurrencyFlag(_uiState.value.currencyCode!!)
        } else {
            "🇯🇵"
        }
    }

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

    // 통화 코드별 환율 정보 반환
    private fun getExchangeRateText(currencyCode: String): String {
        return when (currencyCode) {
            "USD" -> "1340 USD = 1000 원"
            "EUR" -> "1450 EUR = 1000 원"
            "JPY" -> "927 JPY = 1000 원"
            "GBP" -> "1650 GBP = 1000 원"
            "CNY" -> "185 CNY = 1000 원"
            "CAD" -> "1180 CAD = 1000 원"
            "AUD" -> "880 AUD = 1000 원"
            "CHF" -> "1480 CHF = 1000 원"
            "HKD" -> "170 HKD = 1000 원"
            "SGD" -> "980 SGD = 1000 원"
            else -> "100 ${currencyCode} = 1000 원"
        }
    }

    // 통화 코드별 국기 반환
    private fun getCurrencyFlag(currencyCode: String): String {
        return when (currencyCode) {
            "USD" -> "🇺🇸"
            "EUR" -> "🇪🇺"
            "JPY" -> "🇯🇵"
            "GBP" -> "🇬🇧"
            "CNY" -> "🇨🇳"
            "CAD" -> "🇨🇦"
            "AUD" -> "🇦🇺"
            "CHF" -> "🇨🇭"
            "HKD" -> "🇭🇰"
            "SGD" -> "🇸🇬"
            "SEK" -> "🇸🇪"
            "NOK" -> "🇳🇴"
            "NZD" -> "🇳🇿"
            "THB" -> "🇹🇭"
            "VND" -> "🇻🇳"
            "IDR" -> "🇮🇩"
            "MYR" -> "🇲🇾"
            "PHP" -> "🇵🇭"
            "INR" -> "🇮🇳"
            "KRW" -> "🇰🇷"
            "TWD" -> "🇹🇼"
            "BRL" -> "🇧🇷"
            "MXN" -> "🇲🇽"
            "ZAR" -> "🇿🇦"
            "TRY" -> "🇹🇷"
            "RUB" -> "🇷🇺"
            else -> "🏳️"
        }
    }
}