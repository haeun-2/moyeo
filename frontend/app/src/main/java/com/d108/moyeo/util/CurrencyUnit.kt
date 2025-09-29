package com.d108.moyeo.util

data class CurrencyUnit(
    val name: String,   // 한국 원, 미국 달러 ...
    val baseUnit: Int   // 기준 단위 (1 or 100)
)

val currencyUnitMap = mapOf(
    "KRW" to CurrencyUnit("한국 원", 1),
    "USD" to CurrencyUnit("미국 달러", 1),
    "JPY" to CurrencyUnit("일본 엔", 100),
    "EUR" to CurrencyUnit("유럽 유로", 1),
    "GBP" to CurrencyUnit("영국 파운드", 1),
    "CNY" to CurrencyUnit("중국 위안", 1),
    "CHF" to CurrencyUnit("스위스 프랑", 1),
    "CAD" to CurrencyUnit("캐나다 달러", 1)
)
