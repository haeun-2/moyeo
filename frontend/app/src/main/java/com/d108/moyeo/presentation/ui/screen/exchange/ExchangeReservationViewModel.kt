package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ExchangeReservationUiState(
    val currencyCode: String = "",
    val currencyName: String = "",
    val inputAmount: String = "0",
    val exchangeRate: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ExchangeReservationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ExchangeReservationUiState())
    val uiState = _uiState.asStateFlow()

    fun setCurrency(code: String, name: String) {
        _uiState.update {
            it.copy(
                currencyCode = code,
                currencyName = name,
                exchangeRate = getExchangeRateText(code)
            )
        }
    }

    fun onDigitInput(digit: String) {
        val currentAmount = _uiState.value.inputAmount

        if (currentAmount == "0" && digit != "00") {
            _uiState.update { it.copy(inputAmount = digit) }
            return
        }

        if (currentAmount.isEmpty() && digit == "00") return
        if (currentAmount == "0" && digit == "00") return

        if ((currentAmount + digit).length > 10) return

        _uiState.update { it.copy(inputAmount = currentAmount + digit) }
    }

    fun onBackspace() {
        val currentAmount = _uiState.value.inputAmount
        val newAmount = if (currentAmount.length <= 1) "0" else currentAmount.dropLast(1)
        _uiState.update { it.copy(inputAmount = newAmount) }
    }

    fun getCurrencyFlag(currencyCode: String): String {
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

    private fun getExchangeRateText(currencyCode: String): String {
        // TODO: 실제 환율 API에서 가져오기
        return when (currencyCode) {
            "USD" -> "1340 USD"
            "EUR" -> "1450 EUR"
            "JPY" -> "927 JPY"
            "GBP" -> "1650 GBP"
            "CNY" -> "185 CNY"
            "CAD" -> "1180 CAD"
            "AUD" -> "880 AUD"
            "CHF" -> "1480 CHF"
            "HKD" -> "170 HKD"
            "SGD" -> "980 SGD"
            "SEK" -> "145 SEK"
            "NOK" -> "130 NOK"
            "NZD" -> "820 NZD"
            "THB" -> "38 THB"
            "VND" -> "32500 VND"
            "IDR" -> "21000 IDR"
            "MYR" -> "4.7 MYR"
            "PHP" -> "75 PHP"
            "INR" -> "110 INR"
            "KRW" -> "1 KRW"
            "TWD" -> "42 TWD"
            "BRL" -> "7.5 BRL"
            "MXN" -> "27 MXN"
            "ZAR" -> "24 ZAR"
            "TRY" -> "45 TRY"
            "RUB" -> "130 RUB"
            else -> "Not Available"
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}