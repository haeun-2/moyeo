package com.d108.moyeo.data.mapper

import com.d108.moyeo.data.remote.dto.stats.CategoryStatDto
import com.d108.moyeo.data.remote.dto.stats.CategoryStatsDto
import com.d108.moyeo.domain.model.stats.CategoryStat
import com.d108.moyeo.domain.model.stats.CategoryStats

/**
 * CategoryStatDto(서버용 데이터)를 CategoryStat(앱용 데이터) 모델로 변환
 */
fun CategoryStatDto.toDomain(): CategoryStat {
    return CategoryStat(
        categoryId = this.categoryId,
        category = this.category,
        amount = this.amount,
        ratio = this.ratio
    )
}

/**
 * CategoryStatsDto(서버용 데이터)를 CategoryStats(앱용 데이터) 모델로 변환
 */
fun CategoryStatsDto.toDomain(): CategoryStats {
    return CategoryStats(
        totalAmount = this.totalAmount,
        content = this.content.map { it.toDomain() }
    )
}