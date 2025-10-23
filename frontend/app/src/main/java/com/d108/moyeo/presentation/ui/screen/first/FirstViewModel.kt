package com.d108.moyeo.presentation.ui.screen.first

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.usecase.auth.DebugLoginUseCase3
import com.d108.moyeo.domain.usecase.auth.DebugLoginUseCase4
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * FirstScreen의 UI 상태를 담는 데이터 클래스입니다.
 */


/**
 * FirstScreen에서 발생할 수 있는 내비게이션 이벤트를 정의합니다.
 */
sealed class FirstNavigationEvent {
    data object NavigateToHome : FirstNavigationEvent()
    data object NavigateBack : FirstNavigationEvent()
}

@HiltViewModel
class FirstViewModel @Inject constructor(
    private val debugLoginUseCase3: DebugLoginUseCase3, // '디버그 로그인 전문가'를 주입받습니다
    private val debugLoginUseCase4: DebugLoginUseCase4
) : ViewModel() {

    private val _uiState = MutableStateFlow(FirstUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<FirstNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onBackClicked() {
        val currentStep = _uiState.value.currentStep
        val previousStep = when (currentStep) {
            FirstStep.BIOMETRICS -> null
            FirstStep.PIN -> FirstStep.BIOMETRICS
        }
        if (previousStep != null) {
            // 이전 단계가 있으면 상태만 업데이트
            _uiState.update { it.copy(currentStep = previousStep) }
        } else {
            // 첫 단계에서 뒤로가기를 누르면, 화면을 닫으라는 이벤트를 발생시킴
            viewModelScope.launch {
                _navigationEvent.emit(FirstNavigationEvent.NavigateBack)
            }
        }
    }

    /**
     * '디버깅용 3번 로그인' 버튼이 클릭되었을 때 호출됩니다.
     */
    fun onDebugLoginClick3() {
        viewModelScope.launch {
            // 1. 로딩 상태를 true로 변경하여 UI에 로딩 인디케이터를 표시하도록 합니다.
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 2. 주입받은 UseCase를 실행하여 실제 로그인 로직을 수행합니다.
            debugLoginUseCase3()
                .onSuccess { token ->
                    // 3. 성공 시: 로그를 찍고, 홈 화면으로 이동하라는 이벤트를 발생시킵니다.
                    Log.d("FirstViewModel", "3번 디버그 로그인 성공: AccessToken = ${token.accessToken}")
                    _navigationEvent.emit(FirstNavigationEvent.NavigateToHome)
                }
                .onFailure { error ->
                    // 4. 실패 시: 로그를 찍고, 에러 메시지를 상태에 저장하여 UI에 표시하도록 합니다.
                    Log.e("FirstViewModel", "3번 디버그 로그인 실패", error)
                    _uiState.update { it.copy(errorMessage = "3번 로그인에 실패했습니다.") }
                }

            // 5. 성공하든 실패하든, 로딩 상태를 false로 되돌립니다.
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /**
     * '디버깅용 4번 로그인' 버튼이 클릭되었을 때 호출됩니다.
     */
    fun onDebugLoginClick4() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            debugLoginUseCase4()
                .onSuccess { token ->
                    Log.d("FirstViewModel", "4번 디버그 로그인 성공: AccessToken = ${token.accessToken}")
                    _navigationEvent.emit(FirstNavigationEvent.NavigateToHome)
                }
                .onFailure { error ->
                    Log.e("FirstViewModel", "4번 디버그 로그인 실패", error)
                    _uiState.update { it.copy(errorMessage = "4번 로그인에 실패했습니다.") }
                }

            // 5. 성공하든 실패하든, 로딩 상태를 false로 되돌립니다.
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun skipBiometrics() {
        _uiState.update { it.copy(
            currentStep = FirstStep.PIN)
        }
    }

    fun onPinInput(digit: String) {
        if (_uiState.value.pin.length < 6) {
            _uiState.update {
                val newPin = it.pin + digit
                it.copy(pin = newPin, pinError = null)
            }

            // PIN 6자리가 완성되면 즉시 확인
            if (_uiState.value.pin.length == 6) {
                processPin()
            }
        }
    }

    fun onPinBackspace() {
        if (_uiState.value.pin.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    pin = it.pin.dropLast(1),
                    pinError = null
                )
            }
        }
    }

    private fun processPin() {
        when (_uiState.value.pin) {
            "111111" -> onDebugLoginClick3()
            "222222" -> onDebugLoginClick4()
            else -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(pinError = "PIN이 올바르지 않습니다.", pin = "")
                    }
                    delay(2000) // 2초 대기
                    _uiState.update {
                        it.copy(pinError = null)
                    }
                }
            }
        }
    }

    fun onPinClear() {
        _uiState.update {
            it.copy(pin = "", pinError = null)
        }
    }
}