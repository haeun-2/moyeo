package com.d108.moyeo.domain.usecase.history

import com.d108.moyeo.domain.repository.BoxHistoryRepository
import javax.inject.Inject

class GetExchangeHistoryDetailUseCase @Inject constructor(
    private val repository: BoxHistoryRepository
) {
    suspend operator fun invoke(boxId: Long, historyId: Long) = repository.getExchangeHistoryDetail(boxId, historyId)
}