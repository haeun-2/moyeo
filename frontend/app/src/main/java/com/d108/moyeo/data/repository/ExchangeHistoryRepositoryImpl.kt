package com.d108.moyeo.data.repository

import com.d108.moyeo.data.mapper.toDomain
import com.d108.moyeo.data.remote.api.ExchangeHistoryService
import com.d108.moyeo.domain.model.exchange.history.ExchangeRateHistory
import com.d108.moyeo.domain.model.exchange.history.ExchangeVolumeHistory
import com.d108.moyeo.domain.repository.ExchangeHistoryRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.emptyList

@Singleton
class ExchangeHistoryRepositoryImpl @Inject constructor(
    private val exchangeHistoryService: ExchangeHistoryService
): ExchangeHistoryRepository {
    override suspend fun getExchangeRateHistory(
        unit: String?,
        currency: String
    ): Result<List<ExchangeRateHistory>> {
        return runCatching {
            val response = exchangeHistoryService.getExchangeRateHistory(unit, currency)

            if (response.isSuccessful) {
                val dtoList = response.body() ?: emptyList()
                dtoList.map { it.toDomain() }
            } else {
                // 5. API 호출이 실패하면 예외를 발생시킵니다.
                throw Exception("Failed to fetch exchange rate history. Code: ${response.code()}")
            }
        }
    }

    override suspend fun getExchangeVolumeHistory(
        unit: String?,
        currencyType: String
    ): Result<List<ExchangeVolumeHistory>> {
        return runCatching {
            val response = exchangeHistoryService.getExchangeVolumeHistory(unit, currencyType)
            if (response.isSuccessful) {
                val dtoList = response.body() ?: emptyList()
                dtoList.map { it.toDomain() }
            } else {
                throw Exception("Failed to fetch exchange volume history. Code: ${response.code()}")
            }
        }
    }
}