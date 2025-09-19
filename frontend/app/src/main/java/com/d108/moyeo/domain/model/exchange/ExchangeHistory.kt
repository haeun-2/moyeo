package com.d108.moyeo.domain.model.exchange

/**
 * 환율 기록 도메인 모델
 */
data class ExchangeHistory(
    val currencyCode: String,       // 통화 코드
    val buyRate: Double,            // 매수율
    val sellRate: Double,           // 매도율
    val originalRate: Double,       // 기준율
    val period: String,             // 기간 정보
    val chartData: List<ChartData>  // 차트 데이터
)