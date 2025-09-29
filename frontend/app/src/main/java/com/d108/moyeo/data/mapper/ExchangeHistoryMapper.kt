package com.d108.moyeo.data.mapper

import com.d108.moyeo.data.remote.dto.exchange.history.ExchangeRateHistoryResponseDto
import com.d108.moyeo.data.remote.dto.exchange.history.ExchangeVolumeHistoryResponseDto
import com.d108.moyeo.domain.model.exchange.history.ExchangeRateHistory
import com.d108.moyeo.domain.model.exchange.history.ExchangeVolumeHistory

fun ExchangeRateHistoryResponseDto.toDomain() = ExchangeRateHistory(
    buyRate = buyRate,
    sellRate = sellRate,
    originalRate = originalRate,
    period = period,
)


fun ExchangeVolumeHistoryResponseDto.toDomain() = ExchangeVolumeHistory(
    recordedAt = recordedAt,
    totalAmount = totalAmount
)