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

    init { loadNotifications() }

    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getNotifications()
                .onSuccess { list ->
                    val items = list.map { noti ->
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
                    _uiState.update { it.copy(notifications = items, isLoading = false) }
                }
                .onFailure {
                    _uiState.update { it.copy(notifications = emptyList(), isLoading = false) }
                }
        }
    }


}