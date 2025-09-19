package com.d108.moyeo.domain.repository

import com.d108.moyeo.domain.model.exchange.ExchangeHistory
import com.d108.moyeo.domain.model.exchange.ExchangeRate

interface ExchangeRepository {
    suspend fun getCurrentExchangeRates(): Result<List<ExchangeRate>>
    suspend fun getExchangeRateHistory(currency: String, unit: String? = null): Result<ExchangeHistory>
}