package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.payment.QRTokenResponseDto
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface PaymentService {
    @POST("api/payments/qr-code")
    suspend fun generateQRToken(@Query("boxId") boxId: Long
    ): Response<QRTokenResponseDto>

    // TODO: 나중에 실제 결제 요청(/api/payments) API를 여기에 추가해야 합니다.
}