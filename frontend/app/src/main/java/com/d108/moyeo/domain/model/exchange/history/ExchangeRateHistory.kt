package com.d108.moyeo.domain.model.exchange.history


data class ExchangeRateHistory (
    val buyRate: Double,                // 매수율
    val sellRate: Double,               // 매도율
    val originalRate: Double,           // 기준율
    val period: String                  // 기간 정보
)