package com.d108.moyeo.data.remote.dto.box

import com.google.gson.annotations.SerializedName

data class BoxDetailResponseDto(
    @SerializedName("boxId")
    val boxId: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("balances")
    val balances: List<BalanceDto>,
    @SerializedName("type")
    val type: String,
    @SerializedName("permission")
    val permission: PermissionDto,
    @SerializedName("members")
    val members: List<BoxMemberDto>
)