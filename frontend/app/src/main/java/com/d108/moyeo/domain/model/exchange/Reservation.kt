package com.d108.moyeo.domain.model.exchange

data class Reservation(
    val id: Long,
    val fromCurrency: String,
    val toCurrency: String,
    val targetRate: Double, // "기준단위당 KRW" (예: 100 JPY = 900 KRW)
    val amount: Long,       // 서버 정의 그대로 (KRW 또는 외화, BE 계약 기준)
    val expiresAt: String,
    val createdAt: String,
    val status: String
)