package com.d108.moyeo.domain.model.exchange

import java.math.BigDecimal

data class Currency(
    val currencyCode: String,
    val buyRate: BigDecimal,
    val sellRate: BigDecimal,
    val originalRate: BigDecimal,
    val countryFlag: String?
)
