package com.d108.moyeo.domain.repository

import com.d108.moyeo.domain.model.stats.CategoryStats

interface BoxStatisticsRepository {

    /**
     * 서버로부터 특정 박스의 통화별 카테고리 지출 통계를 조회합니다.
     * @return 성공 시 통화 코드를 Key로, 카테고리 통계 정보를 Value로 갖는 Map,
     * 실패 시 에러를 포함하는 Result 객체
     */
    suspend fun getCategoryStats(boxId: Long, startDate: String, endDate: String): Result<Map<String, CategoryStats>>
}