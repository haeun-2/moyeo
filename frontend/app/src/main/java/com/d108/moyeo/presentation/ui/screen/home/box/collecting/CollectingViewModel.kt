package com.d108.moyeo.presentation.ui.screen.home.box.collecting

import androidx.lifecycle.SavedStateHandle
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

sealed class CollectingNavEvent {
    data object NavigateBack : CollectingNavEvent()
    data object ShowBiometricPrompt : CollectingNavEvent()
}

@HiltViewModel
class CollectingViewModel @Inject constructor (
    private val savedStateHandle: SavedStateHandle,
    private val userDataManager: UserDataManager
): ViewModel() {

    private val _uiState = MutableStateFlow(CollectingUiState())
    val uiState = _uiState.asStateFlow()

    // TODO: 임시 정답 핀을 실제 PIN으로 바꾸기
    private val correctPin = "111111"

    private val _navigationEvent = MutableSharedFlow<CollectingNavEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        // NavHost로부터 전달받은 'boxId' 파라미터를 꺼내 상태에 저장
        val initialBoxId = savedStateHandle.get<String>("boxId") ?: "박은 박스 아이디가 널임"
        _uiState.update { it.copy(boxId = initialBoxId) }

        // 만약 전달받은 상태가 있다면 그걸 선택한 상태로 표시해줌
        val initialCurrency = savedStateHandle.get<String>("currencyCode") ?: ""
        if (initialCurrency.isNotBlank()) {
            _uiState.update { currentState ->
                currentState.copy(currency = initialCurrency)
            }
        }

        // 생체인증 여부 반영
        viewModelScope.launch {
            userDataManager.biometricsPreferenceFlow.collect { enabled ->
                _uiState.update { it.copy(biometricsEnabled = enabled) }
            }
        }
    }


    fun onCurrencySelected(currencyCode: String) {
        _uiState.update { it.copy(currency = currencyCode) }
    }

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

    // --- 인증 관련 함수 ---
    fun onBiometricsSucceeded() {
        // TODO: 실제 서버에 충전 요청 API 호출
        _uiState.update { it.copy(currentStep = CollectingStep.FINISH) }
    }

    fun skipBiometrics() {
        _uiState.update { it.copy(currentStep = CollectingStep.PIN) }
    }

    private fun checkPin() {
        viewModelScope.launch {
            if (_uiState.value.pin == correctPin) {
                onPinSucceeded()
            } else {
                val newFailureCount = _uiState.value.pinFailureCount + 1
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
        _uiState.update { it.copy(currentStep = CollectingStep.FINISH) }
    }

    // --- 내비게이션 로직 ---
    fun onNextClicked() {
        when (_uiState.value.currentStep) {
            CollectingStep.CHOOSE_CURRENCY -> {
                _uiState.update { it.copy(currentStep = CollectingStep.HOW_MUCH) }
            }
            CollectingStep.HOW_MUCH -> {
                if (_uiState.value.biometricsEnabled) {
                    _uiState.update { it.copy(currentStep = CollectingStep.BIOMETRIC) }
                    viewModelScope.launch {
                        _navigationEvent.emit(CollectingNavEvent.ShowBiometricPrompt)
                    }
                } else {
                    _uiState.update { it.copy(currentStep = CollectingStep.PIN) }
                }
            }
            CollectingStep.BIOMETRIC -> {
                skipBiometrics()
            }
            CollectingStep.PIN -> {
                if (!_uiState.value.isPinLocked) {
                    checkPin()
                }
            }
            CollectingStep.FINISH -> {
                viewModelScope.launch {
                    _navigationEvent.emit(CollectingNavEvent.NavigateBack)
                }
            }
        }
    }

    fun onBackClick() {
        val currentStep = _uiState.value.currentStep
        if (currentStep == CollectingStep.CHOOSE_CURRENCY || currentStep == CollectingStep.FINISH) {
            viewModelScope.launch {
                _navigationEvent.emit(CollectingNavEvent.NavigateBack)
            }
        } else {
            val previousStep = when (currentStep) {
                CollectingStep.HOW_MUCH -> CollectingStep.CHOOSE_CURRENCY
                CollectingStep.BIOMETRIC, CollectingStep.PIN -> CollectingStep.HOW_MUCH
                else -> currentStep
            }
            _uiState.update { it.copy(currentStep = previousStep) }
        }
    }
}