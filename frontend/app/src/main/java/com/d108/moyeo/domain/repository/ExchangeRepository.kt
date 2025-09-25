package com.d108.moyeo.domain.repository

import com.d108.moyeo.data.remote.dto.exchange.ExchangeRateItem
import com.d108.moyeo.data.remote.dto.exchange.ExchangeReservationResponseDto
import com.d108.moyeo.domain.model.exchange.ExchangeRate

interface ExchangeRepository {
    suspend fun getCurrentExchangeRates(): Result<Map<String, ExchangeRateItem>>

    suspend fun createExchangeReservation(
        boxId: Long,
        fromCurrency: String,
        toCurrency: String,
        amount: Long,
        targetRate: Long,
        expiresAt: String
    ): Result<Unit>

    suspend fun getExchangeReservations(boxId: Long): Result<List<ExchangeReservationResponseDto>>

    suspend fun cancelExchangeReservation(reservationId: String): Result<Unit>

}