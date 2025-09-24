package com.d108.moyeo.domain.repository

import com.d108.moyeo.domain.model.NotificationPage

interface NotificationRepository {
    suspend fun getNotifications(
        page: Int,
        size: Int,
        direction: String = "DESC"
    ): Result<NotificationPage>
}