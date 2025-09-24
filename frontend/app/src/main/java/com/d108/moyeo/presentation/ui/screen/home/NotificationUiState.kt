package com.d108.moyeo.presentation.ui.screen.home

data class NotificationItemUi(
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
    val isLoadingInitial: Boolean = true,   // 최초 로딩
    val isLoadingMore: Boolean = false,     // 추가 로딩(푸터 스피너)
    val hasNext: Boolean = true             // 다음 페이지 존재 여부
)