package com.d108.moyeo.presentation.ui.screen.home.charge

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

sealed class ChargeNavEvent {  // 내비게이션
    data object NavigateBack : ChargeNavEvent()
    data object ShowBiometricPrompt : ChargeNavEvent()
}

@HiltViewModel
class ChargeViewModel @Inject constructor(
    private val userDataManager: UserDataManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChargeUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<ChargeNavEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    // TODO: 임시 정답 핀을 실제 PIN으로 바꾸기
    private val correctPin = "111111"

    init {
        // 생체인증 여부 반영
        viewModelScope.launch {
            userDataManager.biometricsPreferenceFlow.collect { enabled ->
                _uiState.update { it.copy(biometricsEnabled = enabled) }
            }
        }
    }

    // --- 금액 입력 관련 함수 ---
    fun onMoneyDigitInput(digit: String) {
        val currentAmount = _uiState.value.howMuch
        if (currentAmount == "0" && digit != "00") {
            _uiState.update { it.copy(howMuch = digit) }
            return
        }
        if (currentAmount.isEmpty() && digit == "00") return
        _uiState.update { it.copy(howMuch = currentAmount + digit) }
    }

    fun onMoneyBackspace() {
        _uiState.update { it.copy(howMuch = it.howMuch.dropLast(1)) }
    }

    // --- 인증 관련 함수 ---
    fun onBiometricsSucceeded() {
        // TODO: 실제 서버에 충전 요청 API 호출
        _uiState.update { it.copy(currentStep = ChargeStep.FINISH) }
    }

    fun skipBiometrics() {
        _uiState.update { it.copy(currentStep = ChargeStep.PIN) }
    }

    fun onPinInput(digit: String) {
        if (_uiState.value.pin.length < 6) {
            _uiState.update { it.copy(pin = it.pin + digit, pinError = null) }
        }
    }

    fun onPinBackspace() {
        _uiState.update { it.copy(pin = it.pin.dropLast(1), pinError = null) }
    }

    fun onPinClear() {
        _uiState.update { it.copy(pin = "", pinError = null) }
    }

    private fun checkPin() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.pin == correctPin) {
                onPinSucceeded()
            } else {
                val newFailureCount = currentState.pinFailureCount + 1
                if (newFailureCount >= 3) {
                    _uiState.update { it.copy(isPinLocked = true, pinError = "PIN 3회 오류로 잠겼습니다.", pin = "") }
                } else {
                    _uiState.update { it.copy(pinFailureCount = newFailureCount, pinError = "PIN이 일치하지 않습니다. (남은 횟수: ${3 - newFailureCount}회)") }
                    delay(1000L)
                    onPinClear()
                }
            }
        }
    }

    private fun onPinSucceeded() {
        // TODO: 실제 서버에 충전 요청 API 호출
        _uiState.update { it.copy(currentStep = ChargeStep.FINISH) }
    }

    // --- 내비게이션 로직 ---
    fun onNextClicked() {
        when (_uiState.value.currentStep) {
            ChargeStep.HOW_MUCH -> {
                if (_uiState.value.biometricsEnabled) {
                    _uiState.update { it.copy(currentStep = ChargeStep.BIOMETRIC) }
                    viewModelScope.launch {
                        _navigationEvent.emit(ChargeNavEvent.ShowBiometricPrompt)
                    }
                } else {
                    _uiState.update { it.copy(currentStep = ChargeStep.PIN) }
                }
            }
            ChargeStep.BIOMETRIC -> {
                skipBiometrics()
            }
            ChargeStep.PIN -> {
                if (!_uiState.value.isPinLocked) {
                    checkPin()
                }
            }
            ChargeStep.FINISH -> {
                viewModelScope.launch {
                    _navigationEvent.emit(ChargeNavEvent.NavigateBack)
                }
            }
        }
    }

    fun onBackClick() {
        val currentStep = _uiState.value.currentStep
        if (currentStep == ChargeStep.HOW_MUCH || currentStep == ChargeStep.FINISH) {
            viewModelScope.launch {
                _navigationEvent.emit(ChargeNavEvent.NavigateBack)
            }
        } else {
            _uiState.update { it.copy(currentStep = ChargeStep.HOW_MUCH) }
        }
    }

}