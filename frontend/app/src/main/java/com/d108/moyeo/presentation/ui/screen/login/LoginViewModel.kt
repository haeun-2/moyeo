package com.d108.moyeo.presentation.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.data.local.UserDataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginNavigationEvent {
    object NavigateToHome : LoginNavigationEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor (
    private val userDataManager: UserDataManager
) : ViewModel() {

    private val _loginMode = MutableStateFlow(LoginMode.PASSWORD)
    val loginMode = _loginMode.asStateFlow()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<LoginNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val correctPin = "111111" // 사용자 현재 비밀번호 (임시)

    init {
        // 생체인증 여부 반영
        viewModelScope.launch {
            userDataManager.biometricsPreferenceFlow.collect { enabled ->
                _loginMode.value = if (enabled) LoginMode.BIOMETRIC else LoginMode.PASSWORD
            }
        }
    }

    fun switchToPasswordMode() {
        _loginMode.value = LoginMode.PASSWORD
    }

    fun onLoginSuccess() {
        viewModelScope.launch {
            // TODO: 실제 로그인 성공 처리 (토큰 저장 등)
            _navigationEvent.emit(LoginNavigationEvent.NavigateToHome)
        }
    }

    // --- PIN 입력 관련 로직 ---

    fun onPinDigitInput(digit: String) {
        // 6자리가 이미 찼으면 더 이상 입력되지 않도록 함
        if (_uiState.value.pin.length >= 6) return

        val newPin = _uiState.value.pin + digit
        _uiState.update { it.copy(pin = newPin, errorMessage = null) } // 입력 시 에러 메시지 초기화

        // 6자리가 모두 입력되면 자동으로 검증 시작
        if (newPin.length == 6) {
            checkPin(newPin)
        }
    }

    fun onPinBackspace() {
        _uiState.update { it.copy(pin = it.pin.dropLast(1), errorMessage = null) }
    }

    fun onPinClear() {
        _uiState.update { it.copy(pin = "", errorMessage = null) }
    }

    private fun checkPin(pin: String) {
        viewModelScope.launch {
            if (pin == correctPin) {
                // 일치하면 홈으로
                onLoginSuccess()
            } else {
                // 일치하지 않으면 경고하고 다 지움
                _uiState.update { it.copy(errorMessage = "PIN이 일치하지 않습니다.") }
                delay(1000L) // 1초간 메시지를 보여준 후
                onPinClear() // 입력 초기화
            }
        }
    }
}