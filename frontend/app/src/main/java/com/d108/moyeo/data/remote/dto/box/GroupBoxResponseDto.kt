package com.d108.moyeo.data.remote.dto.box

import com.google.gson.annotations.SerializedName

data class GroupBoxResponseDto(
    @SerializedName("boxId")
    val boxId: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("balances")
    val balances: List<BalanceDto>,
    @SerializedName("type")
    val type: String,  // PERSONAL / GROUP
    @SerializedName("isBookmarked")
    val isBookmarked: Boolean
)