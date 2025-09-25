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
            Currency("CHF", "스위스 CHF", "🇨🇭"),
            Currency("JPY", "일본 JPY", "🇯🇵"),
            Currency("EUR", "유럽 EUR", "🇪🇺"),
            Currency("GBP", "영국 GBP", "🇬🇧"),
            Currency("CAD", "캐나다 CAD", "🇨🇦"),
            Currency("USD", "미국 USD", "🇺🇸"),
            Currency("CNY", "중국 CNY", "🇨🇳")
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