package com.d108.moyeo.domain.usecase.exchange

import com.d108.moyeo.domain.model.exchange.Currency
import com.d108.moyeo.domain.repository.ExchangeRepository
import javax.inject.Inject

class GetCurrenciesUseCase @Inject constructor(
    private val repository: ExchangeRepository
) {
    suspend operator fun invoke(): Result<Map<String, Currency>> =
        repository.getCurrencies()
}