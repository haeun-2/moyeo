package com.d108.moyeo.data.remote.dto.account

import com.google.gson.annotations.SerializedName

data class AccountVerificationRequestDto(

    @SerializedName("bankCode")
    val bankCode: String,

    @SerializedName("bankAccount")
    val bankAccount: String
)