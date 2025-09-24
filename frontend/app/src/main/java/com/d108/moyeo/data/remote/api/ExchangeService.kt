package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.exchange.CreateReservationRequestDto
import com.d108.moyeo.data.remote.dto.exchange.ExchangeRateItem
import com.d108.moyeo.data.remote.dto.exchange.ExchangeHistoryResponse
import com.d108.moyeo.data.remote.dto.exchange.ExchangeReservationResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ExchangeService {

    /**
     * 현재 환율 정보 조회
     * GET /api/exchange/rates
     * Map<String, ExchangeRateItem> 형태로 반환
     * key: 통화 코드, value: 환율 정보
     */
    @GET("api/exchange/rates")
    suspend fun getCurrentExchangeRates(): Response<Map<String, ExchangeRateItem>>

    /**
     * 특정 통화의 환율 기록 조회
     * GET /api/exchange/rates/history
     * @param unit 통화 단위 (선택적)
     * @param currency 통화 코드 (필수) - CAD, CHF, CNY, EUR, GBP, JPY, KRW, USD 등
     */
    @GET("api/exchange/rates/history")
    suspend fun getExchangeRateHistory(
        @Query("unit") unit: String? = null,
        @Query("currency") currency: String
    ): Response<ExchangeHistoryResponse>

    /**
     * 환전 예약 생성
     */
    @POST("api/exchanges/reservations")
    suspend fun createExchangeReservation(
        @Body request: CreateReservationRequestDto
    ): Response<Unit>

    /**
     * 사용자의 환전 예약 목록 조회
     */
    @GET("api/exchanges/reservations/{boxId}")
    suspend fun getExchangeReservations(
        @Path("boxId") boxId: Long
    ): Response<List<ExchangeReservationResponseDto>>

    /**
     * 환전 예약 취소
     */
    @DELETE("api/exchanges/reservations/{reservationId}")
    suspend fun cancelExchangeReservation(
        @Path("reservationId") reservationId: String
    ): Response<Unit>

    /**
     * 환전 예약 상태 업데이트
     */
//    @PUT("api/exchanges/reservations/{reservationId}/status")
//    suspend fun updateReservationStatus(
//        @Path("reservationId") reservationId: String,
//        @Body request: UpdateReservationStatusRequestDto
//    ): Response<ExchangeReservationDto>
}