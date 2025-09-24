package com.d108.moyeo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NotificationResponseDto(

    @SerializedName("page")
    val page: Int,

    @SerializedName("size")
    val size: Int,

    @SerializedName("hasNext")
    val hasNext: Boolean,

    @SerializedName("content")
    val content: List<NotificationContentDto>
)
