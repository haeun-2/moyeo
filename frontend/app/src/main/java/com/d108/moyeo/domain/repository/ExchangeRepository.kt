package com.d108.moyeo.domain.repository

import com.d108.moyeo.domain.model.exchange.Currency

interface ExchangeRepository {
    suspend fun exchange(
        fromBoxId: Long,
        fromCurrency: String,
        toCurrency: String,
        amount: Long
    ): Result<Unit>

    suspend fun reservationExchange(
        boxId: Long,
        fromCurrency: String,
        toCurrency: String,
        amount: Long,
        targetRate: Double,
        expiresAt: String
    ): Result<Unit>

    suspend fun getCurrencies(): Result<Map<String, Currency>>
}
