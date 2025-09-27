package com.d108.moyeo.data.remote.dto.exchange

import com.google.gson.annotations.SerializedName

/**
 * 환율 정보 응답 DTO
 * /api/exchange/rates 엔드포인트의 응답
 * Map 형태로 반환됨 (key: 통화코드, value: 환율정보)
 */
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