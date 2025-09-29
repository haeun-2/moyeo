package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.exchange.CurrencyResponseDto
import com.d108.moyeo.data.remote.dto.exchange.ExchangeRequestDto
import com.d108.moyeo.data.remote.dto.exchange.ExchangeReservationRequestDto
import com.d108.moyeo.data.remote.dto.exchange.ReservationResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ExchangeService {
    @POST("/api/exchanges")
    suspend fun exchange(@Body body: ExchangeRequestDto): Response<Unit>

    @POST("/api/exchanges/reservations")
    suspend fun reserve(@Body body: ExchangeReservationRequestDto): Response<Unit>

    @GET("/api/exchanges/rates")
    suspend fun getCurrencies(): Response<Map<String, CurrencyResponseDto>>

    @GET("/api/exchanges/reservations/{boxId}")
    suspend fun getReservationsByBox(@Path("boxId") boxId: Long): Response<List<ReservationResponseDto>>
}
