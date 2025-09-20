package com.d108.moyeo.domain.repository

import com.d108.moyeo.domain.model.Notification

interface NotificationRepository {
    suspend fun getNotifications(): Result<List<Notification>>
}