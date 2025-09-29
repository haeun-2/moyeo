package com.d108.moyeo.domain.repository

import com.d108.moyeo.domain.model.history.ExchangeHistoryDetail
import com.d108.moyeo.domain.model.history.PaginatedHistory // <-- 반환 타입이 도메인 모델로 변경

interface BoxHistoryRepository {
    suspend fun getTransactionHistories(
        boxId: Long,
        startDate: String,
        endDate: String,
        keyword: String,
        type: String,
        categoryId: Long?,
        currency: String,
        page: Int,
        size: Int,
        sortDir: String
    ): Result<PaginatedHistory>


    suspend fun updateHistory(
        boxId: Long,
        historyId: Long,
        memo: String?,
        categoryId: Long?
    ): Result<Unit>

    suspend fun getExchangeHistoryDetail(boxId: Long, historyId: Long): Result<List<ExchangeHistoryDetail>>
}