package com.d108.moyeo.data.repository

import com.d108.moyeo.data.mapper.toDomain
import com.d108.moyeo.data.remote.api.BoxHistoryService
import com.d108.moyeo.domain.model.history.PaginatedHistory
import com.d108.moyeo.domain.repository.BoxHistoryRepository
import javax.inject.Inject

class BoxHistoryRepositoryImpl @Inject constructor(
    private val api: BoxHistoryService // BoxHistoryService 주입
) : BoxHistoryRepository {

    override suspend fun getTransactionHistories(
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
    ): Result<PaginatedHistory> {
        // 기존 BoxRepositoryImpl과 완전히 동일한 패턴으로 에러 처리 및 데이터 변환
        return runCatching {
            val response = api.getTransactionHistories(
                boxId, startDate, endDate, keyword, type, categoryId, currency, page, size, sortDir
            )

            if (response.isSuccessful) {
                // 성공 시, body가 null이 아닌지 확인하고 Domain 모델로 변환
                response.body()?.toDomain()
                    ?: throw Exception("Response body is null")
            } else {
                // 실패 시, 에러 코드와 함께 예외 발생
                throw Exception("Server responded with error: ${response.code()}")
            }
        }
    }
}