package com.d108.moyeo.domain.model.history

data class ExchangeHistoryDetail(
    val fromCurrency: String,
    val fromAmount: Double,
    val toCurrency: String,
    val toAmount: Double,
    val exchangeRate: Double
)