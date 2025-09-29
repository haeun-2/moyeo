package com.d108.moyeo.data.remote.dto.banking

import com.google.gson.annotations.SerializedName

data class ChargeRequestDto(

    @SerializedName("balance")
    val balance: Long
)