package com.d108.moyeo.data.remote.dto.banking

import com.google.gson.annotations.SerializedName

data class ChargeResponseDto (

    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("errorMessage")
    val errorMessage: String?
)