package com.d108.moyeo.data.remote.dto.stats

import com.google.gson.annotations.SerializedName

/**
 * 서버로부터 받는, 특정 통화에 대한 카테고리별 통계 데이터의 구조를 정의하는 DTO 입니다.
 * 이것은 Map<String, CategoryStatsDto> 형태의 응답에서 'Value' 부분에 해당합니다.
 * Key는 국가로, Gson이 자동번역해줌.
 * @property totalAmount 해당 통화의 총 지출액.
 * @property content 카테고리별 상세 내역 리스트.
 */
data class CategoryStatsDto(  // 복수형
    @SerializedName("totalAmount")
    val totalAmount: Double,

    @SerializedName("content")
    val content: List<CategoryStatDto>
)