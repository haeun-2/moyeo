package com.d108.moyeo.domain.usecase.history

import com.d108.moyeo.domain.model.history.PaginatedHistory
import com.d108.moyeo.domain.repository.BoxHistoryRepository
import javax.inject.Inject

class GetTransactionHistoryUseCase @Inject constructor(
    private val repository: BoxHistoryRepository
) {
    suspend operator fun invoke(
        boxId: Long,
        startDate: String,
        endDate: String,
        keyword: String = "",
        type: String = "",
        categoryId: Long? = null,
        currency: String = "",
        page: Int = 0,
        size: Int = 20,
        sortDir: String = "DESC"
    ): Result<PaginatedHistory> {
        // Repository를 호출하고, 그 결과를 그대로 반환합니다.
        return repository.getTransactionHistories(
            boxId, startDate, endDate, keyword, type, categoryId, currency, page, size, sortDir
        )
    }
}