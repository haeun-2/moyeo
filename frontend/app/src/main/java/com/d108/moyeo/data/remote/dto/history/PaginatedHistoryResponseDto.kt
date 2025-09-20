package com.d108.moyeo.data.remote.dto.history

import com.google.gson.annotations.SerializedName

data class PaginatedHistoryResponseDto(
    @SerializedName("page") val page: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("hasNext") val hasNext: Boolean,
    @SerializedName("content") val content: List<HistoryTransactionDto>
)