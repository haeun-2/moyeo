package com.d108.moyeo.domain.usecase.exchange.history

import com.d108.moyeo.domain.repository.ExchangeHistoryRepository
import javax.inject.Inject

class GetExchangeRateHistoryUseCase @Inject constructor(
    private val repository: ExchangeHistoryRepository
) {
    /**
     * 유스케이스를 함수처럼 호출할 수 있게 해주는 operator fun
     * 예: getExchangeRateHistoryUseCase(unit = "1d", currency = "USD")
     */
    suspend operator fun invoke(unit: String?, currency: String) =
        repository.getExchangeRateHistory(unit, currency)
}