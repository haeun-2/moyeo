package com.d108.moyeo.presentation.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.usecase.GetNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotifications: GetNotificationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState = _uiState.asStateFlow()

    private var currentPage = 0
    private var pageSize = 10
    private var loading = false

    init { loadInitial() }

    private fun loadInitial() {
        if (loading) return
        loading = true
        currentPage = 0
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingInitial = true, isLoadingMore = false) }
            getNotifications(currentPage, pageSize)
                .onSuccess { page ->
                    val items = page.notifications.map { noti ->
                        NotificationItemUi(
                            id = noti.transactionId,
                            title = noti.title,
                            time = noti.time,
                            sender = noti.sender,
                            amount = noti.amount,
                            balance = noti.balance,
                            timestamp = noti.receivedAt,
                            boxId = noti.boxId
                        )
                    }
                    _uiState.update {
                        it.copy(
                            notifications = items,
                            isLoadingInitial = false,
                            hasNext = page.hasNext
                        )
                    }
                    if (page.hasNext) currentPage = page.page + 1
                }
                .onFailure {
                    _uiState.update { it.copy(notifications = emptyList(), isLoadingInitial = false, hasNext = false) }
                }
            loading = false
        }
    }

    fun loadMore() {
        if (loading) return
        val state = _uiState.value
        if (!state.hasNext) return

        loading = true
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            getNotifications(currentPage, pageSize)
                .onSuccess { page ->
                    val more = page.notifications.map { noti ->
                        NotificationItemUi(
                            id = noti.transactionId,
                            title = noti.title,
                            time = noti.time,
                            sender = noti.sender,
                            amount = noti.amount,
                            balance = noti.balance,
                            timestamp = noti.receivedAt,
                            boxId = noti.boxId
                        )
                    }
                    _uiState.update {
                        it.copy(
                            notifications = it.notifications + more,
                            isLoadingMore = false,
                            hasNext = page.hasNext
                        )
                    }
                    if (page.hasNext) currentPage = page.page + 1
                }
                .onFailure {
                    _uiState.update { it.copy(isLoadingMore = false) }
                }
            loading = false
        }
    }

    fun refresh() = loadInitial()
}