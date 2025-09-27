package com.d108.moyeo.data.remote.dto.exchange

data class ExchangeReservationRequestDto(
    val boxId: Long,
    val fromCurrency: String,   // 사용자가 입력한 금액의 통화(KRW 또는 예약 외화 코드)
    val toCurrency: String,     // 예약해 둘 외화
    val amount: Long,
    val targetRate: Double,
    val expiresAt: String       // "yyyy-MM-dd"
)