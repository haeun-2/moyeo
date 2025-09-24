package com.d108.moyeo.data.remote.dto.exchange.history

import com.google.gson.annotations.SerializedName

/**
 * 환율 기록 응답 DTO
 * /api/exchange/rates/history 엔드포인트의 응답
 */
data class ExchangeRateHistoryResponseDto(
    @SerializedName("buyRate")
    val buyRate: Double,                // 매수율
    @SerializedName("sellRate")
    val sellRate: Double,               // 매도율
    @SerializedName("originalRate")
    val originalRate: Double,           // 기준율
    @SerializedName("period")
    val period: String                  // 기간 정보
)