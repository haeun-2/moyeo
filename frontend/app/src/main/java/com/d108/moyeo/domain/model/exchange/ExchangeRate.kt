package com.d108.moyeo.domain.model.exchange

/**
 * 환율 정보 도메인 모델
 */
data class ExchangeRate(
    val currencyCode: String,       // 통화 코드 (USD, JPY 등)
    val currencyName: String,       // 통화명 (미국 달러, 일본 엔 등)
    val countryFlag: String,        // 국가 플래그
    val buyRate: Double,            // 매수율
    val sellRate: Double,           // 매도율
    val originalRate: Double,       // 기준율
    val changeRate: String,         // 변화율 (+0.59%, -0.32% 등)
    val isIncreased: Boolean        // 상승/하락 여부
)