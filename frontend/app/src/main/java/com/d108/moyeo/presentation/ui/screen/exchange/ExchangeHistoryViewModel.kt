package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ExchangeHistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ExchangeHistoryUiState())
    val uiState = _uiState.asStateFlow()

    fun loadData(currencyCode: String, mode: String) {
        _uiState.update { it.copy(isLoading = true, currencyCode = currencyCode) }

        // 모드별 샘플 데이터
        when (mode) {
            "charge" -> loadChargeData(currencyCode)
            "refund" -> loadRefundData(currencyCode)
        }

        _uiState.update { it.copy(isLoading = false) }
    }

    private fun loadChargeData(currencyCode: String) {
        val sampleData = when (currencyCode) {
            "USD" -> ExchangeHistoryUiState(
                currencyCode = currencyCode,
                currentRate = "1,340 USD = 1,000 KRW",
                userCount = "1,247명",
                averageAmount = "245만원",
                chartData = listOf(100f, 120f, 90f, 140f, 110f, 160f, 130f)
            )
            "EUR" -> ExchangeHistoryUiState(
                currencyCode = currencyCode,
                currentRate = "1,450 EUR = 1,000 KRW",
                userCount = "892명",
                averageAmount = "189만원",
                chartData = listOf(80f, 95f, 110f, 85f, 120f, 100f, 115f)
            )
            "JPY" -> ExchangeHistoryUiState(
                currencyCode = currencyCode,
                currentRate = "927 JPY = 1,000 KRW",
                userCount = "2,156명",
                averageAmount = "156만원",
                chartData = listOf(120f, 135f, 115f, 145f, 125f, 155f, 140f)
            )
            else -> ExchangeHistoryUiState(
                currencyCode = currencyCode,
                currentRate = "980 ${currencyCode} = 1,000 KRW",
                userCount = "567명",
                averageAmount = "78만원",
                chartData = listOf(60f, 75f, 85f, 70f, 90f, 80f, 95f)
            )
        }

        _uiState.update { sampleData }
    }

    private fun loadRefundData(currencyCode: String) {
        val sampleData = when (currencyCode) {
            "USD" -> ExchangeHistoryUiState(
                currencyCode = currencyCode,
                currentRate = "1,340 USD = 1,000 KRW",
                userCount = "834명",
                averageAmount = "198만원",
                chartData = listOf(90f, 110f, 80f, 130f, 100f, 140f, 120f)
            )
            "EUR" -> ExchangeHistoryUiState(
                currencyCode = currencyCode,
                currentRate = "1,450 EUR = 1,000 KRW",
                userCount = "623명",
                averageAmount = "134만원",
                chartData = listOf(70f, 85f, 95f, 75f, 105f, 90f, 100f)
            )
            "JPY" -> ExchangeHistoryUiState(
                currencyCode = currencyCode,
                currentRate = "927 JPY = 1,000 KRW",
                userCount = "1,789명",
                averageAmount = "123만원",
                chartData = listOf(110f, 125f, 105f, 135f, 115f, 145f, 130f)
            )
            else -> ExchangeHistoryUiState(
                currencyCode = currencyCode,
                currentRate = "980 ${currencyCode} = 1,000 KRW",
                userCount = "445명",
                averageAmount = "67만원",
                chartData = listOf(50f, 65f, 75f, 60f, 80f, 70f, 85f)
            )
        }

        _uiState.update { sampleData }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}