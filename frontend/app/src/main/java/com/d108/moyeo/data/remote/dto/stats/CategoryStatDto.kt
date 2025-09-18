package com.d108.moyeo.data.remote.dto.stats

import com.google.gson.annotations.SerializedName

data class CategoryStatDto (
    @SerializedName("categoryId")
    val categoryId: Long,

    @SerializedName("category")
    val category: String,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("ratio")
    val ratio: Double
)