package com.d108.moyeo.data.repository

import com.d108.moyeo.data.mapper.toDomain
import com.d108.moyeo.data.remote.api.ExchangeService
import com.d108.moyeo.data.remote.dto.exchange.ExchangeRequestDto
import com.d108.moyeo.data.remote.dto.exchange.ExchangeReservationRequestDto
import com.d108.moyeo.domain.model.exchange.Currency
import com.d108.moyeo.domain.repository.ExchangeRepository
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRepositoryImpl @Inject constructor(
    private val api: ExchangeService
) : ExchangeRepository {

    override suspend fun exchange(
        fromBoxId: Long, fromCurrency: String, toCurrency: String, amount: Long
    ): Result<Unit> = runCatching {
        val r = api.exchange(
            ExchangeRequestDto(fromBoxId, fromCurrency, toCurrency, amount)
        )
        if (!r.isSuccessful) throw HttpException(r)
    }

    override suspend fun reservationExchange(
        boxId: Long, fromCurrency: String, toCurrency: String, amount: Long, targetRate: Double, expiresAt: String
    ): Result<Unit> = runCatching {
        val r = api.reserve(
            ExchangeReservationRequestDto(
                boxId = boxId,
                fromCurrency = fromCurrency,
                toCurrency = toCurrency,
                amount = amount,
                targetRate = targetRate,
                expiresAt = expiresAt
            )
        )
        if (!r.isSuccessful) throw HttpException(r)
    }

    override suspend fun getCurrencies(): Result<Map<String, Currency>> = runCatching {
        val response = api.getCurrencies()
        if (!response.isSuccessful) throw HttpException(response)

        val mappedByServerKey: Map<String, Currency> =
            (response.body() ?: emptyMap()).mapValues { entry -> entry.value.toDomain() }

        mappedByServerKey.values.associateBy { it.currencyCode }
    }
}
