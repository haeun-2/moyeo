package com.d108.moyeo.presentation.ui.screen.home.box.calculating

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// TODO: 임시데이터. 랜더링 필요.
data class CalculatingParticipant(
    val id: String,
    val name: String,
    val amount: String,
    val currency: String,
    val isSelected: Boolean = true
)

data class CalculatingUiState(
    val currentStep: CalculatingStep = CalculatingStep.CHOOSE_CURRENCY,
    val boxId: String = "",
    val currency: String = "",

    // 사용자 목록
    val participants: List<CalculatingParticipant> = emptyList(),

    val pin: String = "",
    // PIN 검증을 위한 상태
    val pinFailureCount: Int = 0,
    val isPinLocked: Boolean = false,
    val pinError: String? = null,
)

sealed class CalculatingNavEvent {
    data object NavigateBack : CalculatingNavEvent()
    data object ShowBiometricPrompt : CalculatingNavEvent()
}

class CalculatingViewModel(private val savedStateHandle: SavedStateHandle): ViewModel() {
    private val _uiState = MutableStateFlow(CalculatingUiState())
    val uiState = _uiState.asStateFlow()

    // TODO: 임시 정답 핀을 실제 PIN으로 바꾸기
    private val correctPin = "111111"
    // TODO: 실제로는 DataStore나 SharedPreferences 등에서 이 값을 가져와야 합니다.
    private val isBiometricsEnabledByUser = true

    private val _navigationEvent = MutableSharedFlow<CalculatingNavEvent>()
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

        val participants = listOf(  // TODO: 서버 렌더링
            CalculatingParticipant("1", "김상훈", "12,500", "JPY"),
            CalculatingParticipant("2", "이풍헌", "12,500", "JPY"),
            CalculatingParticipant("3", "박동찬", "12,500", "JPY"),
            CalculatingParticipant("4", "정진우", "12,500", "JPY"),
        )
        _uiState.update { it.copy(participants = participants) }
    }

    // --- 각 단계별 데이터 변경 함수 ---
    fun onCurrencySelected(currencyCode: String) {
        _uiState.update { it.copy(currency = currencyCode) }
    }

    fun onParticipantSelectionChanged(participantId: String, isSelected: Boolean) {
        _uiState.update { currentState ->
            val updatedParticipants = currentState.participants.map {
                if (it.id == participantId) {
                    it.copy(isSelected = isSelected)
                } else {
                    it
                }
            }
            currentState.copy(participants = updatedParticipants)
        }
    }


    fun onPinInput(digit: String) {
        if (_uiState.value.pin.length < 6) {
            _uiState.update { it.copy(pin = it.pin + digit, pinError = null) }
        }
    }

    fun onPinBackspace() { _uiState.update { it.copy(pin = it.pin.dropLast(1), pinError = null) } }
    fun onPinClear() { _uiState.update { it.copy(pin = "", pinError = null) } }

    // --- 인증 관련 함수 ---
    fun onBiometricsSucceeded() {
        // TODO: 실제 서버에 정산 요청 API 호출
        _uiState.update { it.copy(currentStep = CalculatingStep.FINISH) }
    }

    fun skipBiometrics() {
        _uiState.update { it.copy(currentStep = CalculatingStep.PIN) }
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
        // TODO: 실제 서버에 정산 요청 API 호출
        _uiState.update { it.copy(currentStep = CalculatingStep.FINISH) }
    }

    // --- 내비게이션 로직 ---
    fun onNextClicked() {
        when (_uiState.value.currentStep) {
            CalculatingStep.CHOOSE_CURRENCY -> _uiState.update { it.copy(currentStep = CalculatingStep.HOW_TO_CALCULATE) }
            CalculatingStep.HOW_TO_CALCULATE -> {
                if (isBiometricsEnabledByUser) {
                    _uiState.update { it.copy(currentStep = CalculatingStep.BIOMETRIC) }
                    viewModelScope.launch {
                        _navigationEvent.emit(CalculatingNavEvent.ShowBiometricPrompt)
                    }
                } else {
                    _uiState.update { it.copy(currentStep = CalculatingStep.PIN) }
                }
            }
            CalculatingStep.BIOMETRIC -> skipBiometrics()
            CalculatingStep.PIN -> {
                if (!_uiState.value.isPinLocked) {
                    checkPin()
                }
            }
            CalculatingStep.FINISH -> {
                viewModelScope.launch {
                    _navigationEvent.emit(CalculatingNavEvent.NavigateBack)
                }
            }
        }
    }

    fun onBackClick() {
        val currentStep = _uiState.value.currentStep
        if (currentStep == CalculatingStep.CHOOSE_CURRENCY || currentStep == CalculatingStep.FINISH) {
            viewModelScope.launch {
                _navigationEvent.emit(CalculatingNavEvent.NavigateBack)
            }
        } else {
            val previousStep = when (currentStep) {
                CalculatingStep.HOW_TO_CALCULATE -> CalculatingStep.CHOOSE_CURRENCY
                CalculatingStep.BIOMETRIC, CalculatingStep.PIN -> CalculatingStep.HOW_TO_CALCULATE
                else -> currentStep
            }
            _uiState.update { it.copy(currentStep = previousStep) }
        }
    }
}