package com.d108.moyeo.domain.usecase.exchange

import com.d108.moyeo.domain.repository.ExchangeRepository
import javax.inject.Inject

class ExchangeUseCase @Inject constructor(
    private val repo: ExchangeRepository
) {
    suspend operator fun invoke(
        fromBoxId: Long,
        fromCurrency: String,
        toCurrency: String,
        amount: Long
    ) = repo.exchange(fromBoxId, fromCurrency, toCurrency, amount)
}