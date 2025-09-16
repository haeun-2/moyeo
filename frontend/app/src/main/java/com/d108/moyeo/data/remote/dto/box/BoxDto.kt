package com.d108.moyeo.data.remote.dto.box

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BoxDto(
    @SerialName("boxId")
    val boxId: Long,

    @SerialName("name")
    val name: String,

    @SerialName("balances")
    val balances: List<BalanceDto>,

    @SerialName("type")
    val type: String, // "PERSONAL" | "GROUP"
)

@Serializable
data class BalanceDto(
    @SerialName("currency")
    val currency: String,

    @SerialName("balance")
    val balance: Double
)
