package com.d108.moyeo.data.remote.dto.exchange

data class ReservationResponseDto(
    val id: Long,
    val fromCurrency: String,
    val toCurrency: String,
    val targetRate: Double,
    val amount: Long,
    val expiresAt: String,
    val createdAt: String,
    val status: String
)