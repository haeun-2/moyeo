package com.d108.moyeo.presentation.ui.screen.currency.history

import com.d108.moyeo.domain.model.exchange.history.ExchangeRateHistory
import com.d108.moyeo.domain.model.exchange.history.ExchangeVolumeHistory

data class ExchangeHistoryUiState(
    val isLoading: Boolean = true,
    val currencyCode: String = "",
    val currencyName: String = "",
    val exchangeRateHistoryData: List<ExchangeRateHistory> = emptyList(),
    val exchangeVolumeHistoryData: List<ExchangeVolumeHistory> = emptyList(),

    val tradeMode: String = "charge",
    val selectedTimeUnit: String = "10m",

    val errorMessage: String? = null
)
