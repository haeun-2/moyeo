package com.d108.moyeo.presentation.ui.screen.home

data class NotificationItemUi (
    val id: Long,
    val title: String,

    val time: String?,
    val sender: String?,
    val amount: String?,
    val balance: String?,

    val boxId: Long? = null,
    val timestamp: String,
)

data class NotificationUiState(
    val notifications: List<NotificationItemUi> = emptyList(),
    val isLoading: Boolean = true
)