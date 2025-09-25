package com.d108.moyeo.domain.usecase.exchange

import com.d108.moyeo.domain.repository.ExchangeRepository
import javax.inject.Inject

class GetExchangeRatesUseCase @Inject constructor(
    private val repository: ExchangeRepository
){
    suspend operator fun invoke() =
        repository.getCurrentExchangeRates()
}