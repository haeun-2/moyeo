package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.exchange.history.ExchangeRateHistoryResponseDto
import com.d108.moyeo.data.remote.dto.exchange.history.ExchangeVolumeHistoryResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeHistoryService {
    /**
     * 특정 통화의 환율 기록 조회
     * GET /api/exchange/rates/history
     * @param unit 조회 기간. 없으면 10분, 1h 및 1d 선택 가능
     * @param currency 통화 코드 (필수) - CAD, CHF, CNY, EUR, GBP, JPY, KRW, USD 등
     */
    @GET("api/exchanges/rates/histories")
    suspend fun getExchangeRateHistory(
        @Query("unit") unit: String? = null,
        @Query("currency") currency: String
    ): Response<List<ExchangeRateHistoryResponseDto>>


    /**
     * 특정 통화의 거래량 조회
     * Available values : m, h, d
     * Available values : CAD, CHF, CNY, EUR, GBP, JPY, KRW, USD
     */
    @GET("api/exchanges/volumes")
    suspend fun getExchangeVolumeHistory(
        @Query("unit") unit: String? = null,
        @Query("currencyType") currencyType: String
    ): Response<List<ExchangeVolumeHistoryResponseDto>>
}