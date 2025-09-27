package com.d108.moyeo.data.mapper

import com.d108.moyeo.data.remote.dto.exchange.ReservationResponseDto
import com.d108.moyeo.domain.model.exchange.Reservation

fun ReservationResponseDto.toDomain(): Reservation = Reservation(
    id = id,
    fromCurrency = fromCurrency,
    toCurrency = toCurrency,
    targetRate = targetRate,
    amount = amount,
    expiresAt = expiresAt,
    createdAt = createdAt,
    status = status
)