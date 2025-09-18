package com.d108.moyeo.domain.model.stats

/**
 * 특정 통화에 대한 전체 카테고리 통계 정보를 담는 데이터 모델입니다.
 * (예: "KRW"에 대한 통계 정보 전체)
 */
data class CategoryStats(
    val totalAmount: Double,
    val content: List<CategoryStat>
)