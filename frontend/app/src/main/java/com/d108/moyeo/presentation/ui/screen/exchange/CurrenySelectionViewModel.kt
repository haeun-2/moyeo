package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Currency(
    val code: String,
    val name: String,
    val flag: String
)

data class CurrencySelectionUiState(
    val currencies: List<Currency> = emptyList(),
    val selectedCurrency: Currency? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CurrencySelectionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencySelectionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCurrencies()
    }

    private fun loadCurrencies() {
        val currencies = listOf(
            Currency("USD", "미국 USD", "🇺🇸"),
            Currency("EUR", "유럽 EUR", "🇪🇺"),
            Currency("JPY", "일본 JPY", "🇯🇵"),
            Currency("GBP", "영국 GBP", "🇬🇧"),
            Currency("CNY", "중국 CNY", "🇨🇳"),
            Currency("CAD", "캐나다 CAD", "🇨🇦"),
            Currency("AUD", "호주 AUD", "🇦🇺"),
            Currency("CHF", "스위스 CHF", "🇨🇭"),
            Currency("HKD", "홍콩 HKD", "🇭🇰"),
            Currency("SGD", "싱가포르 SGD", "🇸🇬"),
            Currency("SEK", "스웨덴 SEK", "🇸🇪"),
            Currency("NOK", "노르웨이 NOK", "🇳🇴"),
            Currency("NZD", "뉴질랜드 NZD", "🇳🇿"),
            Currency("THB", "태국 THB", "🇹🇭"),
            Currency("VND", "베트남 VND", "🇻🇳"),
            Currency("IDR", "인도네시아 IDR", "🇮🇩"),
            Currency("MYR", "말레이시아 MYR", "🇲🇾"),
            Currency("PHP", "필리핀 PHP", "🇵🇭"),
            Currency("INR", "인도 INR", "🇮🇳"),
            Currency("KRW", "한국 KRW", "🇰🇷"),
            Currency("TWD", "대만 TWD", "🇹🇼"),
            Currency("BRL", "브라질 BRL", "🇧🇷"),
            Currency("MXN", "멕시코 MXN", "🇲🇽"),
            Currency("ZAR", "남아프리카 ZAR", "🇿🇦"),
            Currency("TRY", "터키 TRY", "🇹🇷"),
            Currency("RUB", "러시아 RUB", "🇷🇺")
        )

        _uiState.update { it.copy(currencies = currencies) }
    }

    fun selectCurrency(currency: Currency) {
        _uiState.update { it.copy(selectedCurrency = currency) }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedCurrency = null) }
    }

    fun isConfirmEnabled(): Boolean {
        return _uiState.value.selectedCurrency != null
    }

    fun getSelectedCurrency(): Currency? {
        return _uiState.value.selectedCurrency
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}