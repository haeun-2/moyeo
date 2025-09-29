package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.stats.CategoryStatsDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 박스 통계(BoxStatistics) 관련 API 명세를 정의하는 Retrofit 서비스 인터페이스
 */
interface BoxStatisticsService {

    /**
     * 특정 박스의 통화별 카테고리 지출 통계를 조회합니다.
     * @param boxId 통계를 조회할 박스의 ID
     * @param startDate 조회 시작일 ("YY-MM-DD" 형식)
     * @param endDate 조회 종료일 ("YY-MM-DD" 형식)
     * @return 성공 시, 통화 코드를 Key로, 카테고리 통계 정보를 Value로 갖는 Map을 담은 Response 객체
     */
    @GET("api/boxes/{boxId}/transactions/stats/categories")
    suspend fun getCategoryStats(
        @Path("boxId") boxId: Long,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<Map<String, CategoryStatsDto>>
}