package com.d108.moyeo.presentation.ui.screen.home.sending

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 이체 화면의 상태(State)를 정의합니다.
 *
 * @property currentStep 현재 진행 중인 이체 단계
 * @property currency 사용자가 선택한 보낼 화폐 (예: "KRW", "USD")
 * @property targetBox 사용자가 선택한 보낼 박스 ID
 * @property howMuch 사용자가 입력한 보낼 금액
 */
data class SendingUiState(
    val currentStep: SendingStep = SendingStep.CHOOSE_CURRENCY,
    val currency: String = "",
    val targetBox: String = "",
    val howMuch: String = "",
    val pin: String = ""
)

sealed class SendingNavEvent {
    data object NavigateBack : SendingNavEvent()
    data object ShowBiometricPrompt : SendingNavEvent() // 생체 인증 창을 띄우라는 이벤트 추가
}


class SendingViewModel(
    // NavHost에서 전달해준 파라미터('currencyId')를 받기 위해 SavedStateHandle를 사용
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val _uiState = MutableStateFlow(SendingUiState())
    val uiState = _uiState.asStateFlow()

    // 사용자가 생체 인식을 활성화했는지 여부.
    // TODO: 실제로는 DataStore나 SharedPreferences 등에서 이 값을 가져와야 합니다.
    private val isBiometricsEnabledByUser = true

    /**
     * 내비게이션 이벤트를 UI에 전달하기 위한 SharedFlow입니다.
     * Channel의 일종으로, 한 번 발생한 이벤트를 놓치지 않고 UI에 전달할 때 유용합니다.
     */
    private val _navigationEvent = MutableSharedFlow<SendingNavEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    // ViewModel이 처음 생성될 때 실행되는 초기화 블록
    init {
        // NavHost로부터 전달받은 'currencyId' 파라미터를 꺼냄
        // navigate("sending/{currencyId}") 에서의 "currencyId"와 이름이 같아야 함.
        val initialCurrency = savedStateHandle.get<String>("currencyId") ?: ""  // 할당함

        // 파라미터로 받은 값이 있다면, 초기 상태의 currency 값으로 설정
        if (initialCurrency.isNotBlank()) {
            _uiState.update { currentState ->
                currentState.copy(currency = initialCurrency)
            }
        }
    }


    // 2. 각 단계에서 사용자가 입력한 값을 SendingUiState에 반영하는 함수
    /**
     * 사용자가 보낼 화폐를 선택했을 때 호출됩니다.
     */
    fun onCurrencySelected(currency: String) {
        _uiState.update { currentState ->
            currentState.copy(currency = currency)
        }
    }

    /**
     * 사용자가 보낼 박스를 선택했을 때 호출됩니다.
     */
    fun onTargetBoxSelected(boxId: String) {
        _uiState.update { currentState ->
            currentState.copy(targetBox = boxId)
        }
    }

    /**
     * 사용자가 보낼 금액을 입력할 때마다 호출.
     */
    fun onMoneyDigitInput(digit: String) {
        val currentAmount = _uiState.value.howMuch
        if (currentAmount == "0" && digit != "00") {
            _uiState.update { it.copy(howMuch = digit) }
            return
        }
        if (currentAmount.isEmpty() && digit == "00") return
//        if ((currentAmount + digit).length > 10) return // 최대 10자리 제한

        _uiState.update { it.copy(howMuch = currentAmount + digit) }
    }

    fun onMoneyBackspace() {
        _uiState.update { it.copy(howMuch = it.howMuch.dropLast(1)) }
    }

    // 생체 인증에 성공
    fun onBiometricsSucceeded() {
        // TODO: 실제 서버에 이체 요청 API 호출
        // API 호출 성공 시 FINISH 단계로 이동
        _uiState.update { it.copy(currentStep = SendingStep.FINISH) }
    }

    /**
     * 사용자가 생체 인증을 건너뛰거나 취소했을 때 호출됩니다.
     */
    fun skipBiometrics() {
        // PIN 입력 단계로 이동합니다.
        _uiState.update { it.copy(currentStep = SendingStep.PIN) }
    }


    /**
     * 핀 입력 검사
     **/

    fun onPinChanged(pin: String) {
        // TODO: 6자리 등 PIN 길이에 대한 유효성 검사 추가 가능
        _uiState.update { it.copy(pin = pin) }
    }



    fun onPinSucceeded() {
        // TODO: 실제 서버에 이체 요청 API 호출
        // API 호출 성공 시 FINISH 단계로 이동
        _uiState.update { it.copy(currentStep = SendingStep.FINISH) }
    }


    // 3. 사용자가 "다음" 버튼을 눌렀을 때 호출될 함수
    /**
     * 현재 단계를 확인하고 다음 단계로 상태를 변경
     */
    fun onNextClicked() {
        when (_uiState.value.currentStep) {
            SendingStep.CHOOSE_CURRENCY -> {
                _uiState.update { it.copy(currentStep = SendingStep.TARGET_BOX) }
            }
            SendingStep.TARGET_BOX -> {
                _uiState.update { it.copy(currentStep = SendingStep.HOW_MUCH) }
            }
            SendingStep.HOW_MUCH -> {
                // 금액 입력 후 다음 버튼을 누르면 인증을 시작합니다.
                // 사용자가 생체 인식을 설정했는지 확인합니다.
                if (isBiometricsEnabledByUser) {
                    // 설정했다면, BIOMETRIC 단계로 상태를 바꾸고
                    _uiState.update { it.copy(currentStep = SendingStep.BIOMETRIC) }
                    // 화면에 생체 인증 창을 띄우라는 이벤트를 보냅니다.
                    viewModelScope.launch {
                        _navigationEvent.emit(SendingNavEvent.ShowBiometricPrompt)
                    }
                } else {
                    // 설정하지 않았다면, 바로 PIN 입력 단계로 넘어갑니다.
                    _uiState.update { it.copy(currentStep = SendingStep.PIN) }
                }
            }
            SendingStep.BIOMETRIC -> {  // 이 버튼은 사용자가 다 실패하면 뜸
                skipBiometrics()  // 스킵하고 핀 인증
            }
            SendingStep.PIN -> {
                // TODO: 입력된 PIN(_uiState.value.pin)이 올바른지 검증
                // PIN 검증 성공 시, 실제 서버에 이체 요청 API 호출
                // API 호출 성공 시 FINISH 단계로 이동
                _uiState.update { it.copy(currentStep = SendingStep.FINISH) }
            }
            SendingStep.FINISH -> {
                // 완료 화면에서 버튼을 누르면 화면 닫기
                viewModelScope.launch {
                    _navigationEvent.emit(SendingNavEvent.NavigateBack)
                }
            }
        }
    }


    fun onBackClick() {
        val currentStep = _uiState.value.currentStep

        if (currentStep == SendingStep.CHOOSE_CURRENCY || currentStep == SendingStep.FINISH) {
            viewModelScope.launch {
                _navigationEvent.emit(SendingNavEvent.NavigateBack)
            }
        } else {
            val previousStep = when (currentStep) {
                SendingStep.TARGET_BOX -> SendingStep.CHOOSE_CURRENCY
                SendingStep.HOW_MUCH -> SendingStep.TARGET_BOX
                SendingStep.BIOMETRIC, SendingStep.PIN -> SendingStep.HOW_MUCH // 인증 단계에서는 금액 입력으로
                else -> currentStep
            }
            _uiState.update { it.copy(currentStep = previousStep) }
        }
    }

}