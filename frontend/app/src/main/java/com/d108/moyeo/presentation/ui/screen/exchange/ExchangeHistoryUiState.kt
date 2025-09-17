package com.d108.moyeo.presentation.ui.screen.exchange


data class ExchangeHistoryUiState(
    val currencyCode: String = "",
    val currentRate: String = "",
    val userCount: String = "",
    val averageAmount: String = "",
    val chartData: List<Float> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)