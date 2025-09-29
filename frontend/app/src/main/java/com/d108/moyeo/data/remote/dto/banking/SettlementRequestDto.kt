package com.d108.moyeo.data.remote.dto.banking

import com.google.gson.annotations.SerializedName

data class SettlementRequestDto(
    @SerializedName("boxMemberId")
    val boxMemberId: Long,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("currencyType")
    val currencyType: String

)
