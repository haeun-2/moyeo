package com.d108.moyeo.data.remote.dto.box

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// GET /api/boxes/me 응답 전용 DTO
data class PersonalBoxResponseDto(
    @SerializedName("boxId")
    val boxId: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("balances")
    val balances: List<BalanceDto>,

    @SerializedName("type")
    val type: String, // "PERSONAL" | "GROUP"
)