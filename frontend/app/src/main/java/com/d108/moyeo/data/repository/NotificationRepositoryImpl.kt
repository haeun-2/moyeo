package com.d108.moyeo.data.repository

import com.d108.moyeo.data.mapper.toDomain
import com.d108.moyeo.data.remote.api.NotificationService
import com.d108.moyeo.domain.model.Notification
import com.d108.moyeo.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val api: NotificationService
) : NotificationRepository {

    override suspend fun getNotifications(): Result<List<Notification>> = runCatching {
        val res = api.getNotifications()
        if (res.isSuccessful) {
            res.body()?.map { it.toDomain() } ?: emptyList()
        } else {
            error("Server error: ${res.code()}")
        }
    }
}