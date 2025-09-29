package com.d108.moyeo.presentation.ui.screen.home.transfer

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.core.BoxStore
import com.d108.moyeo.core.BoxStoreUiState
import com.d108.moyeo.data.local.UserDataManager
import com.d108.moyeo.domain.model.box.Balance
import com.d108.moyeo.domain.usecase.banking.TransferUseCase
import com.d108.moyeo.domain.usecase.box.GetPersonalBoxUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat
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
    private val boxStore: BoxStore,
    private val userDataManager: UserDataManager
) : ViewModel() {

    // BoxStore 에서 내 통화 목록을 불러옴
    val currencies = boxStore.personalCurrencies

    // init 블록에서 초기화 -> 내 박스가 아닌 박스만 선택 가능하도록 수정
    private val _selectableBoxes = MutableStateFlow<List<BoxStoreUiState>>(emptyList())
    val selectableBoxes = _selectableBoxes.asStateFlow()

    // TODO: 임시 정답 핀을 찐핀으로 바꾸기
    // 임시 정답 PIN 추가
    private val correctPin = "111111"

    private val _uiState = MutableStateFlow(TransferUiState(mode = TransferMode.TRANSFER))
    val uiState = _uiState.asStateFlow()

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
    private var myPersonalBalances: List<Balance> = emptyList() // 내 개인 박스의 모든 통화 잔액을 저장할 변수

    private var isSubmitting: Boolean = false


    // ViewModel이 처음 생성될 때 실행되는 초기화 블록
    init {

        // nav 인자 파싱 (mode, targetBoxId, currencyId)
        val modeArg = (savedStateHandle.get<String>("mode") ?: "TRANSFER").uppercase()
        val initialMode = runCatching { TransferMode.valueOf(modeArg) }.getOrElse { TransferMode.TRANSFER }
        val initialTargetBoxId = savedStateHandle.get<Long>("targetBoxId") ?: -1L
        val initialCurrency = savedStateHandle.get<String>("currencyId") ?: ""

        // 모드 별 시작 스텝 설정
        val startStep = if (initialMode == TransferMode.DEPOSIT) {
            TransferStep.CHOOSE_CURRENCY
        } else {
            TransferStep.TARGET_BOX
        }

        // 초기 상태 세팅
        _uiState.update {
            it.copy(
                mode = initialMode,
                currentStep = startStep,
                targetBox = initialTargetBoxId,
                currency = initialCurrency
            )
        }

        viewModelScope.launch {
            getPersonalBox()
                .onSuccess {
                    box ->
                        myPersonalBoxId = box.id
                        myPersonalBalances = box.balances
                        onCurrencySelected(initialCurrency)

                    }
                .onFailure { /* TODO: 에러 처리 */}

            // 내 박스가 아닌 박스로만 이체할 수 있도록 필터링
            boxStore.boxUiStates.collect { allBoxes ->
                _selectableBoxes.value = allBoxes.filter { it.id != myPersonalBoxId }
            }

            // 생체인증 여부 반영
            userDataManager.biometricsPreferenceFlow.collect { enabled ->
                _uiState.update { it.copy(biometricsEnabled = enabled) }
            }
        }
    }

    // 2. 각 단계에서 사용자가 입력한 값을 TransferUiState에 반영하는 함수
    /**
     * 사용자가 보낼 화폐를 선택했을 때 호출됩니다.
     */
    fun onCurrencySelected(currency: String) {
        // 내 잔액 목록에서 선택된 통화의 잔액을 찾음
        val balanceAmount = myPersonalBalances.find { it.currency == currency }?.balance ?: 0.0

        // 금액을 천 단위로 포맷팅합니다. (소수점 포함)
        val decimalFormat = DecimalFormat("#,##0.####")
        val formattedBalance = decimalFormat.format(balanceAmount)
        val balanceText = "잔액: $formattedBalance $currency"
        _uiState.update { currentState ->
            currentState.copy(
                currency = currency,
                myBalance = balanceText
            )
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

        // 1. 새로 입력될 전체 금액 문자열
        val newAmountStr = if (currentAmount == "0" && digit != "00") {
            digit
        } else {
            currentAmount + digit
        }

        // 2. 초기 입력 값 및 길이 제한 등 유효성 검사
        if (currentAmount.isEmpty() && digit == "00") return
        if (newAmountStr.length > 10) return // 최대 10자리 제한 << 10억

        // 3. 잔액 정보 로드
        val balance = myPersonalBalances.find { it.currency == uiState.value.currency }?.balance ?: 0.0
        val maxAmount = balance.toLong() // 정수 부분만 비교

        // 4. 새로 만들어진 금액(newAmountStr)이 잔액보다 큰지 확인
        val newAmountLong = newAmountStr.toLongOrNull() ?: 0L
        if (newAmountLong > maxAmount) {

            // 초과하면 통장 최대금액으로 업데이트
            _uiState.update { it.copy(howMuch = maxAmount.toString()) }
            return
        }

        // 모든 검사를 통과한 경우에만 상태를 업데이트합니다.
        _uiState.update { it.copy(howMuch = newAmountStr) }
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

        // 오류 분기 처리
        val action = if (state.mode == TransferMode.DEPOSIT) "입금" else "이체"

        Log.d("TransferViewModel", "mode = $action, fromBoxId=$from, toBoxId=$to, amount=$amount, currency=$currency")

        // 입력 검증
        if (from == null || amount == null || currency.isBlank()) {
            _uiState.update { it.copy(pinError = "$action 정보가 올바르지 않습니다.") }
            return
        }

        isSubmitting = true
        viewModelScope.launch {
            sendTransfer(fromBoxId = from, toBoxId = to, currency = currency, amount = amount)
                .onSuccess {
                    _uiState.update { it.copy(currentStep = TransferStep.FINISH) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(pinError = "$action 실패: ${e.message ?: "알 수 없는 오류"}") }
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
                viewModelScope.launch {
                    val isEnabled = userDataManager.biometricsPreferenceFlow.first()
                    if (isEnabled) {
                        _uiState.update { it.copy(currentStep = TransferStep.BIOMETRIC) }
                        _navigationEvent.emit(TransferNavEvent.ShowBiometricPrompt)
                    } else {
                        _uiState.update { it.copy(currentStep = TransferStep.PIN) }
                    }
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
        val rootStep = if (_uiState.value.mode == TransferMode.DEPOSIT) {
            TransferStep.CHOOSE_CURRENCY
        } else {
            TransferStep.TARGET_BOX
        }

        if (currentStep == rootStep || currentStep == TransferStep.FINISH) {
            viewModelScope.launch {
                _navigationEvent.emit(TransferNavEvent.NavigateBack)
            }
        } else {
            val previousStep = when (currentStep) {
                TransferStep.CHOOSE_CURRENCY -> if (
                    _uiState.value.mode == TransferMode.TRANSFER
                ) {
                    TransferStep.TARGET_BOX
                } else rootStep
                TransferStep.HOW_MUCH -> TransferStep.CHOOSE_CURRENCY
                TransferStep.BIOMETRIC, TransferStep.PIN -> TransferStep.HOW_MUCH // 인증 단계에서는 금액 입력으로
                else -> currentStep
            }
            _uiState.update { it.copy(currentStep = previousStep) }
        }
    }

}