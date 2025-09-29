package com.d108.moyeo.data.remote.dto.signup

import com.google.gson.annotations.SerializedName

data class SignUpRequestDto(
    @SerializedName("sessionId")
    val sessionId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("fid")
    val fid: String,

    @SerializedName("connectedBankCode")
    val connectedBankCode: String,

    @SerializedName("connectedBankAccount")
    val connectedBankAccount: String
)