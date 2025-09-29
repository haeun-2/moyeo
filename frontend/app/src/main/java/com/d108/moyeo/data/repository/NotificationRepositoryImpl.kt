package com.d108.moyeo.data.repository

import com.d108.moyeo.data.mapper.toDomain
import com.d108.moyeo.data.remote.api.NotificationService
import com.d108.moyeo.domain.model.NotificationPage
import com.d108.moyeo.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val api: NotificationService
) : NotificationRepository {

    override suspend fun getNotifications(
        page: Int,
        size: Int,
        direction: String
    ): Result<NotificationPage> = runCatching {
        val res = api.getNotifications(page, size, direction)
        if (!res.isSuccessful) error("Server error: ${res.code()}")

        val body = res.body() ?: error("Empty body")
        NotificationPage(
            page = body.page,
            size = body.size,
            hasNext = body.hasNext,
            notifications = body.content.map { it.toDomain() }
        )
    }
}