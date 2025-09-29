package com.d108.moyeo.domain.model

data class NotificationPage(
    val page: Int,
    val size: Int,
    val hasNext: Boolean,
    val notifications: List<Notification>
)