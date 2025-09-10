package com.d108.moyeo.presentation.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// TODO: 이 데이터 클래스는 domain/model 패키지로 이동해야 합니다.
data class Notification(
    val id: String,
    val title: String,
    val content: String,
    val timestamp: String,
    val isRead: Boolean
)

// NotificationScreen의 UI 상태를 담는 데이터 클래스
data class NotificationUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = true
)

class NotificationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // ViewModel이 생성될 때 임시 데이터 로드.
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            // 실제 앱에서는 Repository를 통해 서버 API를 호출합니다.
            delay(1500L) // 1.5초간의 가상 로딩 딜레이

            val dummyNotifications = listOf(
                Notification("1", "입금 완료", "'상훈 풍헌 동찬 일본 여행'동찬 일본 여행'동찬 일본 여행'동찬 일본 여행'동찬 일본 여행'동찬 일본 여행'동찬 일본 여행'동찬 일본 여행'동찬 일본 여행' 박스에 50,000원이 입금되었어요.", "1일 전", false),
                Notification("2", "친구 초대", "김상훈님이 '미국 도대체 언제 감' 박스에 초대했어요.", "2일 전", false),
                Notification("3", "환전 완료", "1,500 USD 환전이 정상적으로 처리되었습니다.", "5일 전", true),
                Notification("4", "목표 달성!", "'오아시스' 박스의 목표 금액을 달성했어요! 축하합니다.", "7일 전", true)
            )

            // 만약 알림이 없는 시나리오를 테스트하고 싶다면, 아래 주석을 해제하세요.
            // val dummyNotifications = emptyList<Notification>()

            _uiState.update { it.copy(notifications = dummyNotifications, isLoading = false) }
        }
    }
}