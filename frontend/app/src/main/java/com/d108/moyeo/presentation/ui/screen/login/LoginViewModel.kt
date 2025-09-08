package com.d108.moyeo.presentation.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class LoginMode {
    BIOMETRIC,
    PASSWORD
}

sealed class LoginNavigationEvent {
    object NavigateToHome : LoginNavigationEvent()
}

class LoginViewModel : ViewModel() {

    private val _loginMode = MutableStateFlow(LoginMode.BIOMETRIC)
    val loginMode = _loginMode.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<LoginNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    // 생체 인증이 취소되거나 실패하면 이 함수를 호출하여 비밀번호 모드로 전환
    fun switchToPasswordMode() {
        _loginMode.value = LoginMode.PASSWORD
    }

    // 생체 인증 또는 비밀번호 로그인이 성공하면 호출
    fun onLoginSuccess() {
        viewModelScope.launch {
            // TODO: 실제 로그인 성공 처리 (토큰 저장 등)
            _navigationEvent.emit(LoginNavigationEvent.NavigateToHome)
        }
    }
}