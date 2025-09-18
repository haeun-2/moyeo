package com.d108.moyeo.data.remote.dto.box

import com.google.gson.annotations.SerializedName

data class PermissionDto(
    @SerializedName("boxId")
    val boxId: Long,
    @SerializedName("canTransfer")
    val canTransfer: Boolean,
    @SerializedName("canPayment")
    val canPayment: Boolean,
    @SerializedName("canExchange")
    val canExchange: Boolean,
    @SerializedName("isOwner")
    val isOwner: Boolean
)