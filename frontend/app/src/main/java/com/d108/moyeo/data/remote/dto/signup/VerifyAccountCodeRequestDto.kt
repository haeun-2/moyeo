package com.d108.moyeo.data.remote.dto.signup

import com.google.gson.annotations.SerializedName

data class VerifyAccountCodeRequestDto(
    @SerializedName("sessionId")
    val sessionId: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("bankAccount")
    val bankAccount: String,

    @SerializedName("verificationCode")
    val accountCode: String
)