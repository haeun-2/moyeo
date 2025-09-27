package com.d108.moyeo.data.mapper

import com.d108.moyeo.data.remote.dto.exchange.CurrencyResponseDto
import com.d108.moyeo.domain.model.exchange.Currency
import java.math.BigDecimal

fun CurrencyResponseDto.toDomain(): Currency = Currency(
    currencyCode = currencyCode,
    buyRate = BigDecimal.valueOf(buyRate),
    sellRate = BigDecimal.valueOf(sellRate),
    originalRate = BigDecimal.valueOf(originalRate),
    countryFlag = countryFlag
)

fun Currency.toUi(): CurrencyResponseDto = CurrencyResponseDto(
    currencyCode = currencyCode,
    buyRate = buyRate.toDouble(),
    sellRate = sellRate.toDouble(),
    originalRate = originalRate.toDouble(),
    countryFlag = countryFlag ?: ""
)