package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.fcm.FcmTokenRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

interface FcmTokenService {
    @PUT("api/fcm/tokens")
    suspend fun registerFcmToken(@Body request: FcmTokenRequestDto): Response<Unit>
}