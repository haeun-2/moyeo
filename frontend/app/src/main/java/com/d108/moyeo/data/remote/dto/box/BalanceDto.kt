package com.d108.moyeo.data.remote.dto.box

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BalanceDto(
    @SerialName("currency")
    val currency: String,

    @SerialName("balance")
    val balance: Double
)
