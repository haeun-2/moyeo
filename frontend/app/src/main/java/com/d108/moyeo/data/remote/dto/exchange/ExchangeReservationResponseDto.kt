package com.d108.moyeo.data.remote.dto.exchange

import com.google.gson.annotations.SerializedName

data class ExchangeReservationResponseDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("fromCurrency")
    val fromCurrency: String,
    @SerializedName("toCurrency")
    val toCurrency: String,
    @SerializedName("targetRate")
    val targetRate: Double,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("expiresAt")
    val expiresAt: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("status")
    val status: String
)