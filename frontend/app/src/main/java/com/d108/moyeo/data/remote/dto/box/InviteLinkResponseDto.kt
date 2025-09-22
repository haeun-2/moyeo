package com.d108.moyeo.data.remote.dto.box

import com.google.gson.annotations.SerializedName

data class InviteLinkResponseDto(

    @SerializedName("inviteLink")
    val inviteLink: String,

    @SerializedName("inviteCode")
    val inviteCode: String,

    @SerializedName("expiresAt")
    val expiresAt: String
)