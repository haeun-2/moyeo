package com.d108.moyeo.data.repository

import com.d108.moyeo.data.mapper.toDomain
import com.d108.moyeo.data.remote.api.BoxStatisticsService
import com.d108.moyeo.domain.model.stats.CategoryStats
import com.d108.moyeo.domain.repository.BoxStatisticsRepository
import javax.inject.Inject

class BoxStatisticsRepositoryImpl @Inject constructor(
    private val boxStatisticsService: BoxStatisticsService
) : BoxStatisticsRepository {

    override suspend fun getCategoryStats(boxId: Long, startDate: String, endDate: String): Result<Map<String, CategoryStats>> {
        return runCatching {
            val response = boxStatisticsService.getCategoryStats(boxId, startDate, endDate)
            if (response.isSuccessful) {
                // Map의 각 value(CategoryStatsDto)를 toDomain()으로 변환하여 새로운 Map을 만듭니다.
                response.body()?.mapValues { entry -> entry.value.toDomain() } ?: emptyMap()
            } else {
                throw Exception("Server responded with error: ${response.code()}")
            }
        }
    }
}