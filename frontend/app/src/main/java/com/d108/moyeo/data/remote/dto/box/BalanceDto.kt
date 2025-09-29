package com.d108.moyeo.data.remote.dto.box

import com.google.gson.annotations.SerializedName

data class BalanceDto(
    @SerializedName("currency")
    val currency: String,

    @SerializedName("balance")
    val balance: Double
)
