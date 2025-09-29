package com.d108.moyeo.domain.usecase.history

import com.d108.moyeo.domain.repository.BoxHistoryRepository
import javax.inject.Inject

class UpdateHistoryUseCase @Inject constructor(
    private val repository: BoxHistoryRepository
) {
    suspend operator fun invoke(
        boxId: Long,
        historyId: Long,
        memo: String? = null, // memo만 수정할 경우
        categoryId: Long? = null // categoryId만 수정할 경우
    ): Result<Unit> {
        return repository.updateHistory(
            boxId = boxId,
            historyId = historyId,
            memo = memo,
            categoryId = categoryId
        )
    }
}