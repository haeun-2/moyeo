package com.d108.moyeo.data.remote.dto.exchange

import com.google.gson.annotations.SerializedName

data class UpdateReservationStatusRequestDto(
    @SerializedName("status")
    val status: String,
    @SerializedName("reason")
    val reason: String? = null
)