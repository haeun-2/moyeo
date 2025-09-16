package com.d108.moyeo.data.remote.dto.box

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName

data class BalanceDto(
    @SerializedName("currency")
    val currency: String,

    @SerializedName("balance")
    val balance: Double
)
