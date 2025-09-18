package com.d108.moyeo.domain.usecase.stats

import com.d108.moyeo.domain.model.stats.CategoryStats
import com.d108.moyeo.domain.repository.BoxStatisticsRepository
import javax.inject.Inject

/**
 * 특정 박스의 통화별 카테고리 통계를 조회하는 비즈니스 로직을 담당하는 UseCase
 * @param boxStatisticsRepository
 */
class GetCategoryStatsUseCase @Inject constructor(
    private val boxStatisticsRepository: BoxStatisticsRepository
) {
    /**
     * 이 클래스의 인스턴스를 함수처럼 호출할 수 있게 해주는 'operator fun invoke' 입니다.
     */
    suspend operator fun invoke(
        boxId: Long,
        startDate: String,
        endDate: String
    ): Result<Map<String, CategoryStats>> {
        return boxStatisticsRepository.getCategoryStats(boxId, startDate, endDate)
    }
}