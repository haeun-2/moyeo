package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.NotificationResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface NotificationService {

    @GET("/api/notifications")
    suspend fun getNotifications(): Response<List<NotificationResponseDto>>
}