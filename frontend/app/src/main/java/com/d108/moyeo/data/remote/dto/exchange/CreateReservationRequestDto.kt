package com.d108.moyeo.data.remote.dto.exchange

import com.google.gson.annotations.SerializedName

data class CreateReservationRequestDto(
    @SerializedName("boxId")
    val boxId: Long,
    @SerializedName("fromCurrency")
    val fromCurrency: String,
    @SerializedName("toCurrency")
    val toCurrency: String,
    @SerializedName("amount")
    val amount: Long,
    @SerializedName("targetRate")
    val targetRate: Double,
    @SerializedName("expiresAt")
    val expiresAt: String,
){
    init{
        require(amount >= 100) {"거래 금액은 최소 100 이상이어야 합니다."}
    }
}