package com.d108.moyeo.domain.usecase.exchange.history

import com.d108.moyeo.domain.repository.ExchangeHistoryRepository
import javax.inject.Inject

class GetBuyExchangeVolumeHistoryUseCase @Inject constructor (
    private val repository: ExchangeHistoryRepository
){
    suspend operator fun invoke(unit: String?, currencyType: String) =
        repository.getBuyExchangeVolumeHistory(unit, currencyType)
}