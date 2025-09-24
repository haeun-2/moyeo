package com.d108.moyeo.data.remote.dto.account

import com.google.gson.annotations.SerializedName

data class ConnectedAccountResponseDto(

    @SerializedName("bankName")
    val bankName: String,

    @SerializedName("bankLogoImg")
    val bankLogoImg: String?,

    @SerializedName("bankAccount")
    val bankAccount: String
)