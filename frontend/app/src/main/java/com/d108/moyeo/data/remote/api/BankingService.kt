package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.banking.ChargeRequestDto
import com.d108.moyeo.data.remote.dto.banking.ChargeResponseDto
import com.d108.moyeo.data.remote.dto.banking.TransferRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 이체, 입금(Banking) 관련 API 명세를 정의하는 Retrofit 서비스 인터페이스입니다.
 */
interface BankingService {

    // 이체
    @POST("/api/transfer")
    suspend fun transfer(@Body body: TransferRequestDto): Response<Unit>

    // 충전
    @POST("/api/banks/deposit")
    suspend fun charge(@Body body: ChargeRequestDto): Response<ChargeResponseDto>
}