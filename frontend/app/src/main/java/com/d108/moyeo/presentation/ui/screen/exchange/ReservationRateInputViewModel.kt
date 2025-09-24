package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.repository.ExchangeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReservationRateInputUiState(
    val currencyCode: String = "",
    val currencyName: String = "",
    val inputRate: String = "0",
    val currentRate: String = "",
    val errorMessage: String? = null
)

@HiltViewModel
class ReservationRateInputViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationRateInputUiState())
    val uiState = _uiState.asStateFlow()

    fun setCurrency(code: String, name: String) {
        _uiState.update {
            it.copy(
                currencyCode = code,
                currencyName = name
            )
        }
        loadCurrentRate(code)
    }

    private fun loadCurrentRate(currencyCode: String) {
        viewModelScope.launch {
            exchangeRepository.getCurrentExchangeRates()
                .onSuccess { ratesMap ->
                    val targetRateItem = ratesMap[currencyCode]
                    _uiState.update {
                        it.copy(
                            currentRate = targetRateItem?.let { rateItem ->
                                "${rateItem.originalRate.toInt()} ${currencyCode} = 1000원"
                            } ?: "환율 정보 없음"
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            currentRate = "환율 로드 실패",
                            errorMessage = exception.message
                        )
                    }
                }
        }
    }

    fun onDigitInput(digit: String) {
        val currentRate = _uiState.value.inputRate

        if (currentRate == "0" && digit != "00") {
            _uiState.update { it.copy(inputRate = digit) }
            return
        }

        if (currentRate.isEmpty() && digit == "00") return
        if (currentRate == "0" && digit == "00") return
        if ((currentRate + digit).length > 10) return

        _uiState.update { it.copy(inputRate = currentRate + digit) }
    }

    fun onBackspace() {
        val currentRate = _uiState.value.inputRate
        val newRate = if (currentRate.length <= 1) "0" else currentRate.dropLast(1)
        _uiState.update { it.copy(inputRate = newRate) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}