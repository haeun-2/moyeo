package com.d108.moyeo.data.remote.dto.history

import com.google.gson.annotations.SerializedName

data class UpdateHistoryRequestDto(
    @SerializedName("memo") val memo: String?,
    @SerializedName("categoryId") val categoryId: Long?
)