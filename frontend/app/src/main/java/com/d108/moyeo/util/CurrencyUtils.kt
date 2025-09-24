package com.d108.moyeo.util

object CurrencyUtils {
    fun getCurrencyName(code: String): String =
        when (code) {
            "KRW" -> "한국 원"
            "USD" -> "미국 달러"
            "JPY" -> "일본 엔"
            "EUR" -> "유럽 유로"
            "GBP" -> "영국 파운드"
            "CNY" -> "중국 위안"
            "CHF" -> "스위스 프랑"
            "CAD" -> "캐나다 달러"
            else -> code
        }
}