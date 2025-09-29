package com.d108.moyeo.domain.usecase

import com.d108.moyeo.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repo: NotificationRepository
) {
    suspend operator fun invoke(page: Int, size: Int) =
        repo.getNotifications(page = page, size = size, direction = "DESC")
}