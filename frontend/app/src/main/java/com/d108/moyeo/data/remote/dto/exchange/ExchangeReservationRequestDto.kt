package com.d108.moyeo.data.remote.dto.exchange

import com.google.gson.annotations.SerializedName

data class ExchangeReservationRequestDto(

    @SerializedName("boxId")
    val boxId: Long,

    @SerializedName("fromCurrency")
    val fromCurrency: String,   // 사용자가 입력한 금액의 통화(KRW 또는 예약 외화 코드)

    @SerializedName("toCurrency")
    val toCurrency: String,     // 예약해 둘 외화

    @SerializedName("amount")
    val amount: Long,

    @SerializedName("targetRate")
    val targetRate: Double,

    @SerializedName("expiresAt")
    val expiresAt: String       // "yyyy-MM-dd"
)