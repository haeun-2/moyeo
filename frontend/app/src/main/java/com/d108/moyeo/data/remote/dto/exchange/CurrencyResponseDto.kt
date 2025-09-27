package com.d108.moyeo.data.remote.dto.exchange

import com.google.gson.annotations.SerializedName

data class CurrencyResponseDto(

    @SerializedName("currencyCode")
    val currencyCode: String,           // 통화 코드 (예: "CAD", "USD")

    @SerializedName("buyRate")
    val buyRate: Double,                // 매수율

    @SerializedName("sellRate")
    val sellRate: Double,               // 매도율

    @SerializedName("originalRate")
    val originalRate: Double,           // 기준율

    @SerializedName("countryFlag")
    val countryFlag: String             // 국가 플래그 (문자열)
)