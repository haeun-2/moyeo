package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.NotificationResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NotificationService {

    @GET("/api/notifications")
    suspend fun getNotifications(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("direction") direction: String = "DESC"
    ): Response<NotificationResponseDto>
}