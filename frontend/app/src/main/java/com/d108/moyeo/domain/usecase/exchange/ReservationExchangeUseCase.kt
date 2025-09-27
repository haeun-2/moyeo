package com.d108.moyeo.domain.usecase.exchange

import com.d108.moyeo.domain.repository.ExchangeRepository
import javax.inject.Inject

class ReservationExchangeUseCase @Inject constructor(
    private val repo: ExchangeRepository
) {
    suspend operator fun invoke(
        boxId: Long,
        fromCurrency: String,
        toCurrency: String,
        amount: Long,
        targetRate: Double,
        expiresAt: String
    ) = repo.reservationExchange(boxId, fromCurrency, toCurrency, amount, targetRate, expiresAt)
}