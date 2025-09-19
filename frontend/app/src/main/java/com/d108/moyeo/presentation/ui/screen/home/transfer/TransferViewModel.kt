package com.d108.moyeo.presentation.ui.screen.home.transfer

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.core.BoxStore
import com.d108.moyeo.domain.usecase.banking.TransferUseCase
import com.d108.moyeo.domain.usecase.box.GetPersonalBoxUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TransferNavEvent {
    data object NavigateBack : TransferNavEvent()
    data object ShowBiometricPrompt : TransferNavEvent() // 생체 인증 창을 띄우라는 이벤트 추가
}

@HiltViewModel
class TransferViewModel @Inject constructor(
    // NavHost에서 전달해준 파라미터('currencyId')를 받기 위해 SavedStateHandle를 사용
    private val savedStateHandle: SavedStateHandle,
    private val getPersonalBox: GetPersonalBoxUseCase,
    private val sendTransfer: TransferUseCase,
    private val boxStore: BoxStore
) : ViewModel() {

    // BoxStore 에서 모임 박스 목록, 내 통화 목록을 불러옴
    val groupBoxesUi = boxStore.groupBoxesUi
    val currencies = boxStore.personalCurrencies

    // TODO: 임시 정답 핀을 찐핀으로 바꾸기
    // 임시 정답 PIN 추가
    private val correctPin = "111111"

    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState = _uiState.asStateFlow()

    // 사용자가 생체 인식을 활성화했는지 여부.
    // TODO: 실제로는 DataStore나 SharedPreferences 등에서 이 값을 가져와야 합니다.
    private val isBiometricsEnabledByUser = true

    /**
     * 내비게이션 이벤트를 UI에 전달하기 위한 SharedFlow입니다.
     * Channel의 일종으로, 한 번 발생한 이벤트를 놓치지 않고 UI에 전달할 때 유용합니다.
     */
    private val _navigationEvent = MutableSharedFlow<TransferNavEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    // TODO: (1/2) ViewModel이 생성될 때, DataStore를 확인하여
    //  - 현재 PIN이 잠금 상태인지,
    //  - 잠겼다면 남은 시간은 몇 초인지 확인하는 로직이 필요합니다.
    //  - val remainingLockTime = checkPinLockStatusUseCase()
    //  - if (remainingLockTime > 0) { _uiState.update { it.copy(isPinLocked = true, ...) } }

    // 내 통장 ID
    private var myPersonalBoxId: Long? = null
    private var isSubmitting: Boolean = false


    // ViewModel이 처음 생성될 때 실행되는 초기화 블록
    init {
        // NavHost로부터 전달받은 'currencyId' 파라미터를 꺼냄
        // navigate("transfer/{currencyId}") 에서의 "currencyId"와 이름이 같아야 함.
        val initialCurrency = savedStateHandle.get<String>("currencyId") ?: ""  // 할당함

        // 파라미터로 받은 값이 있다면, 초기 상태의 currency 값으로 설정
        if (initialCurrency.isNotBlank()) {
            _uiState.update { currentState ->
                currentState.copy(currency = initialCurrency)
            }
        }

        //
        viewModelScope.launch {
            getPersonalBox()
                .onSuccess { box -> myPersonalBoxId = box.id }
                .onFailure { /* TODO: 에러 처리 */}
        }
    }

    // 2. 각 단계에서 사용자가 입력한 값을 TransferUiState에 반영하는 함수
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
    fun onTargetBoxSelected(boxId: Long) {
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
        submitTransfer()
    }

    /**
     * 사용자가 생체 인증을 건너뛰거나 취소했을 때 호출됩니다.
     */
    fun skipBiometrics() {
        // PIN 입력 단계로 이동합니다.
        _uiState.update { it.copy(currentStep = TransferStep.PIN) }
    }


    /**
     * 핀 입력 검사
     **/

    // TODO: 실제 핀이랑 일치하는지 여부 및 5회 틀리면 잠금 기능 추가. 임시데이터로 비교해보자.
    fun onPinInput(digit: String) {
        if (_uiState.value.pin.length < 6) {
            _uiState.update { it.copy(pin = it.pin + digit) }
        }
    }

    fun onPinBackspace() {
        _uiState.update { it.copy(pin = it.pin.dropLast(1)) }
    }

    fun onPinClear() {
        _uiState.update { it.copy(pin = "") }
    }

    private fun checkPin() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.pin == correctPin) {
                // PIN 검증 성공
                _uiState.update { it.copy(pinFailureCount = 0, pinError = null) }
                onPinSucceeded()
            } else {
                // PIN 검증 실패
                val newFailureCount = currentState.pinFailureCount + 1
                if (newFailureCount >= 3) {
                    // 3회 이상 실패 시 잠금
                    _uiState.update {
                        it.copy(
                            isPinLocked = true,
                            pinError = "PIN 3회 오류로 잠겼습니다.",
                            pin = ""
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            pinFailureCount = newFailureCount,
                            pinError = "PIN이 일치하지 않습니다. (남은 횟수: ${3 - newFailureCount}회)"
                        )
                    }
                    delay(1000L) // 1초 후 입력 필드 초기화
                    onPinClear()
                }
            }
        }
    }

    fun onPinSucceeded() {
        submitTransfer()
    }


    /** 실제 API 호출 */
    private fun submitTransfer() {
        if (isSubmitting) return
        val state = _uiState.value
        val from = myPersonalBoxId
        val to = state.targetBox
        val amount = state.howMuch.toLongOrNull()
        val currency = state.currency

        Log.d("TransferViewModel", "fromBoxId=$from, toBoxId=$to, amount=$amount, currency=$currency")

        // 입력 검증
        if (from == null || to == null || amount == null || currency.isBlank()) {
            _uiState.update { it.copy(pinError = "이체 정보가 올바르지 않습니다.") }
            return
        }

        isSubmitting = true
        viewModelScope.launch {
            sendTransfer(fromBoxId = from, toBoxId = to, currency = currency, amount = amount)
                .onSuccess {
                    _uiState.update { it.copy(currentStep = TransferStep.FINISH) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(pinError = "이체 실패: ${e.message ?: "알 수 없는 오류"}") }
                }
            isSubmitting = false
        }
    }


    // 3. 사용자가 "다음" 버튼을 눌렀을 때 호출될 함수
    /**
     * 현재 단계를 확인하고 다음 단계로 상태를 변경
     */
    fun onNextClicked() {
        when (_uiState.value.currentStep) {
            TransferStep.TARGET_BOX -> {
                _uiState.update { it.copy(currentStep = TransferStep.CHOOSE_CURRENCY) }
            }
            TransferStep.CHOOSE_CURRENCY -> {
                _uiState.update { it.copy(currentStep = TransferStep.HOW_MUCH) }
            }
            TransferStep.HOW_MUCH -> {
                // 금액 입력 후 다음 버튼을 누르면 인증을 시작합니다.
                // 사용자가 생체 인식을 설정했는지 확인합니다.
                if (isBiometricsEnabledByUser) {
                    // 설정했다면, BIOMETRIC 단계로 상태를 바꾸고
                    _uiState.update { it.copy(currentStep = TransferStep.BIOMETRIC) }
                    // 화면에 생체 인증 창을 띄우라는 이벤트를 보냅니다.
                    viewModelScope.launch {
                        _navigationEvent.emit(TransferNavEvent.ShowBiometricPrompt)
                    }
                } else {
                    // 설정하지 않았다면, 바로 PIN 입력 단계로 넘어갑니다.
                    _uiState.update { it.copy(currentStep = TransferStep.PIN) }
                }
            }
            TransferStep.BIOMETRIC -> {  // 이 버튼은 사용자가 다 실패하면 뜸
                skipBiometrics()  // 스킵하고 핀 인증
            }
            TransferStep.PIN -> {
                if (!_uiState.value.isPinLocked) {
                    checkPin() // PIN 검증 로직 호출
                }
            }
            TransferStep.FINISH -> {
                // 완료 화면에서 버튼을 누르면 화면 닫기
                viewModelScope.launch {
                    _navigationEvent.emit(TransferNavEvent.NavigateBack)
                }
            }
        }
    }


    fun onBackClick() {
        val currentStep = _uiState.value.currentStep

        if (currentStep == TransferStep.TARGET_BOX || currentStep == TransferStep.FINISH) {
            viewModelScope.launch {
                _navigationEvent.emit(TransferNavEvent.NavigateBack)
            }
        } else {
            val previousStep = when (currentStep) {
                TransferStep.CHOOSE_CURRENCY -> TransferStep.TARGET_BOX
                TransferStep.HOW_MUCH -> TransferStep.CHOOSE_CURRENCY
                TransferStep.BIOMETRIC, TransferStep.PIN -> TransferStep.HOW_MUCH // 인증 단계에서는 금액 입력으로
                else -> currentStep
            }
            _uiState.update { it.copy(currentStep = previousStep) }
        }
    }

}