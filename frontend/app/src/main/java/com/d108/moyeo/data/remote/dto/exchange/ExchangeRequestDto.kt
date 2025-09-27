package com.d108.moyeo.data.remote.dto.exchange

data class ExchangeRequestDto(
    val fromBoxId: Long,
    val fromCurrency: String,
    val toCurrency: String,
    val amount: Long
)