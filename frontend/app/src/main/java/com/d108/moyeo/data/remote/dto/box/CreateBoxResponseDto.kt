package com.d108.moyeo.data.remote.dto.box

import com.google.gson.annotations.SerializedName

data class CreateBoxResponseDto (
    @SerializedName("boxId")
    val boxId: Long
)