package com.d108.moyeo.domain.model.stats

data class CategoryStat(
    val categoryId: Long,
    val category: String,
    val amount: Double,
    val ratio: Double
)