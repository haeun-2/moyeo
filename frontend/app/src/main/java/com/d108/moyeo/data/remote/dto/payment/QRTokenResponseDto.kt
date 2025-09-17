package com.d108.moyeo.data.remote.dto.payment

import com.google.gson.annotations.SerializedName


data class QRTokenResponseDto(
    @SerializedName("token")
    val token: String
)