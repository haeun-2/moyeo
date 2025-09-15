package com.d108.moyeo.data.remote.dto
import com.google.gson.annotations.SerializedName

data class BankDto(
    @SerializedName("bankCode")
    val bankCode: String,

    @SerializedName("bankName")
    val bankName: String,

    @SerializedName("encodedLogoImg")  // 서버에서 널로 넘겨줄 수 있음
    val encodedBankLogoImg: String?
)