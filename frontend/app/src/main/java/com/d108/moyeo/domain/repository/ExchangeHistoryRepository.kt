package com.d108.moyeo.domain.repository

import com.d108.moyeo.domain.model.exchange.history.ExchangeRateHistory
import com.d108.moyeo.domain.model.exchange.history.ExchangeVolumeHistory

interface ExchangeHistoryRepository {
    suspend fun getExchangeRateHistory(
        unit: String?,  // 1h, 1d
        currency: String  // KRW, ..
    ): Result<List<ExchangeRateHistory>>

    suspend fun getExchangeVolumeHistory(
        unit: String?,
        currencyType: String
    ): Result<List<ExchangeVolumeHistory>>
}