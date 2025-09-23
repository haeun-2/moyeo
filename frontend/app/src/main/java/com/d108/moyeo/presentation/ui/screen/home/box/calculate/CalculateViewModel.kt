package com.d108.moyeo.presentation.ui.screen.home.box.calculate

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.core.BoxStore
import com.d108.moyeo.data.remote.dto.banking.SettlementRequestDto
import com.d108.moyeo.domain.usecase.box.GetBoxMembersUseCase
import com.d108.moyeo.domain.usecase.box.SettleBoxUseCase
import com.d108.moyeo.presentation.ui.component.home.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.abs


sealed class CalculateNavEvent {
    data object NavigateBack : CalculateNavEvent()
    data object ShowBiometricPrompt : CalculateNavEvent()
}

@HiltViewModel
class CalculateViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getBoxMembersUseCase: GetBoxMembersUseCase,
    private val boxStore: BoxStore,
    private val settleBoxUseCase: SettleBoxUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(CalculateUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<CalculateNavEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    val boxId = savedStateHandle.get<Long>("boxId") ?: -1L

    init {
        if (boxId != -1L) {
            loadInitialData(boxId)
        }
    }

    /**
     * 화면에 필요한 초기 데이터(멤버 목록, 정산 가능 통화)를 비동기적으로 불러옵니다.
     */
    private fun loadInitialData(boxId: Long) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            // 멤버 목록과 박스 상세 정보를 동시에 요청
            val membersResultDeferred = async { getBoxMembersUseCase(boxId) }
            val boxInfoDeferred = async { boxStore.boxUiStates.firstOrNull()?.find { it.id == boxId } }

            val membersResult = membersResultDeferred.await()
            val boxInfo = boxInfoDeferred.await()

            membersResult.onSuccess { members ->  // 멤버 리스트를 잘 받았다면
                val settlementParticipants = members.map { boxMember ->
                    SettlementParticipant(
                        member = boxMember,
                        amount = 0.0,
                        amountStr = "0",
                        isManuallyEdited = false,
                        isEnabled = true
                    )
                }
                val availableCurrencies = boxInfo?.balances
                    ?.filter { it.balance > 0 }   // UiState에서 잔액이 0 이상인 것 중
                    ?.map { Currency(it.currency, "") }
                    ?: emptyList()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        participants = members,  // 유스케이스로 받은 원본
                        settlementParticipants = settlementParticipants, // UI용
                        availableCurrencies = availableCurrencies,
                        boxInfo = boxInfo,
                        selectedMemberIds = members.map { member -> member.id }.toSet() // 기본으로 모두 선택
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "멤버 정보를 불러올 수 없습니다.") }
            }
        }
    }


    // TODO: 임시 정답 핀을 실제 PIN으로 바꾸기
    private val correctPin = "111111"

    // TODO: 실제로는 DataStore나 SharedPreferences 등에서 이 값을 가져와야 합니다.
    private val isBiometricsEnabledByUser = true

    // --- 각 단계별 데이터 변경 함수 ---
    fun onCurrencySelected(currencyCode: String) {
        val currentSelection = _uiState.value.selectedCurrencies
        // 이미 선택된 통화면 Set에서 제거, 아니면 추가 (토글 방식)
        val newSelection = if (currentSelection.contains(currencyCode)) {
            currentSelection - currencyCode
        } else {
            currentSelection + currencyCode
        }
        _uiState.update { it.copy(selectedCurrencies = newSelection) }
    }

    fun onParticipantSelectionChanged(participantId: Long, isSelected: Boolean) {
        val currentState = _uiState.value
        val newSelection = if (isSelected) {
            currentState.selectedMemberIds + participantId
        } else {
            currentState.selectedMemberIds - participantId
        }

        _uiState.update {
            it.copy(selectedMemberIds = newSelection)
        }

        // 체크박스 상태 변경 후 전체 재계산
        recalculateAllAmounts()
    }

    private fun recalculateAllAmounts() {
        val currentState = _uiState.value

        // **핵심 수정: 체크박스 변경 시 모든 수동 편집 상태를 초기화하고 처음부터 재분배**
        val selectedParticipants = currentState.settlementParticipants.filter {
            currentState.selectedMemberIds.contains(it.member.id)
        }

        val participantCount = selectedParticipants.size.takeIf { it > 0 } ?: 1
        val owner = selectedParticipants.find { it.member.permission.isOwner }
        val baseAmount = currentState.totalSettlementAmount / participantCount
        val remainder = currentState.totalSettlementAmount - (baseAmount * participantCount)

        val updatedParticipants = currentState.settlementParticipants.map { participant ->
            when {
                // 선택되지 않은 멤버: 0원, 편집 불가, 수동편집 상태 초기화
                !currentState.selectedMemberIds.contains(participant.member.id) -> {
                    participant.copy(
                        amount = 0.0,
                        amountStr = "0",
                        isManuallyEdited = false, // 수동편집 상태 초기화
                        isEnabled = false
                    )
                }
                // 선택된 멤버: 처음부터 재분배, 수동편집 상태 초기화
                else -> {
                    val finalAmount = if (participant.member.id == owner?.member?.id) {
                        baseAmount + remainder
                    } else {
                        baseAmount
                    }

                    participant.copy(
                        amount = finalAmount,
                        amountStr = finalAmount.toFormattedString(),
                        isManuallyEdited = false, // 수동편집 상태 초기화
                        isEnabled = true
                    )
                }
            }

        }

        val updatedTotal = updatedParticipants.sumOf { it.amount }
        if (abs(updatedTotal - uiState.value.totalSettlementAmount) < 0.001) {
            _uiState.update { it.copy(isSettlementSumValid = true) }
        } else {
            _uiState.update { it.copy(isSettlementSumValid = false) }
        }

        _uiState.update { it.copy(settlementParticipants = updatedParticipants) }
    }

    fun onParticipantAmountChanged(participantId: Long, newAmountStr: String) {
        val currentState = _uiState.value
        if (currentState.settlementQueue.isEmpty()) return

        // 선택되지 않은 멤버는 편집 불가
        if (!currentState.selectedMemberIds.contains(participantId)) return

        val newAmount = newAmountStr.replace(",", "").toDoubleOrNull()?.takeIf { it >= 0.0 } ?: 0.0


        // **핵심: 선택된 멤버들만 대상으로 계산**
        val selectedParticipants = currentState.settlementParticipants.filter {
            currentState.selectedMemberIds.contains(it.member.id)
        }

        // 수동 편집되지 않은 선택된 멤버들
        val nonEditedSelectedParticipants = selectedParticipants.filter {
            !it.isManuallyEdited || it.member.id == participantId
        }

        // **수동 편집되지 않은 사람이 1명만 남으면 편집 불가**
        if (nonEditedSelectedParticipants.size == 1 &&
            nonEditedSelectedParticipants.first().member.id == participantId &&
            selectedParticipants.any { it.isManuallyEdited && it.member.id != participantId }) {
            return // 편집 불가
        }

        // 체크박스에 1명만 남으면 편집 불가
        if (selectedParticipants.size == 1)
            return

        // 현재 입력하려는 사용자를 제외한 수동 편집된 금액 합계
        val otherManuallyEditedSum = selectedParticipants
            .filter { it.isManuallyEdited && it.member.id != participantId }
            .sumOf { it.amount }

        // **최대 입력 가능 금액 계산**
        val maxPossibleAmount = currentState.totalSettlementAmount - otherManuallyEditedSum
        val constrainedAmount = if (newAmount > maxPossibleAmount) {
            maxPossibleAmount
        } else {
            newAmount
        }

        // 1. 현재 수정된 참가자 업데이트
        var updatedParticipants = currentState.settlementParticipants.map { participant ->
            if (participant.member.id == participantId) {
                participant.copy(
                    amount = constrainedAmount,
                    amountStr = constrainedAmount.toFormattedString(),
                    isManuallyEdited = true
                )
            } else {
                participant
            }
        }

        // 2. 수동 편집된 전체 금액 재계산
        val totalManuallyEditedSum = updatedParticipants
            .filter {
                it.isManuallyEdited &&
                        currentState.selectedMemberIds.contains(it.member.id)
            }
            .sumOf { it.amount }

        // 3. 남은 금액을 수동 편집되지 않은 선택된 멤버들에게 분배
        val remainingAmount = currentState.totalSettlementAmount - totalManuallyEditedSum
        val nonEditedSelected = updatedParticipants.filter {
            !it.isManuallyEdited &&
                    currentState.selectedMemberIds.contains(it.member.id)
        }

        if (nonEditedSelected.isNotEmpty()) {
            val baseAmount = remainingAmount / nonEditedSelected.size
            val remainder = remainingAmount - (baseAmount * nonEditedSelected.size)
            val ownerInNonEdited = nonEditedSelected.find { it.member.permission.isOwner }

            updatedParticipants = updatedParticipants.map { participant ->
                if (nonEditedSelected.any { it.member.id == participant.member.id }) {
                    val finalAmount = if (participant.member.id == ownerInNonEdited?.member?.id) {
                        baseAmount + remainder
                    } else {
                        baseAmount
                    }
                    participant.copy(
                        amount = finalAmount,
                        amountStr = finalAmount.toFormattedString()
                    )
                } else {
                    participant
                }
            }
        }

        // 4. 선택되지 않은 멤버들은 0원으로 설정
        updatedParticipants = updatedParticipants.map { participant ->
            if (!currentState.selectedMemberIds.contains(participant.member.id)) {
                participant.copy(amount = 0.0, amountStr = "0")
            } else {
                participant
            }
        }

        _uiState.update { it.copy(settlementParticipants = updatedParticipants) }
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
        submitSettlement()
    }

    fun skipBiometrics() {
        _uiState.update { it.copy(currentStep = CalculateStep.PIN) }
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
        submitSettlement()
    }

    // --- 내비게이션 로직 ---
    fun onNextClicked() {
        val currentState = _uiState.value
        when (currentState.currentStep) {
            CalculateStep.CHOOSE_CURRENCY -> {
                val queue = currentState.selectedCurrencies.toList()
                _uiState.update { it.copy(
                    settlementQueue = queue,
                    currentSettlementIndex = 0
                )}
                prepareSettlementForCurrentCurrency() // 첫 통화 정산 시작
                _uiState.update { it.copy(currentStep = CalculateStep.HOW_TO_CALCULATE) }

            }
            CalculateStep.HOW_TO_CALCULATE -> {
                val currentCurrency = currentState.settlementQueue[currentState.currentSettlementIndex]
                val finalParticipantsForCurrency = currentState.settlementParticipants
                val updatedDetails = currentState.allSettlementDetails + (currentCurrency to finalParticipantsForCurrency)

                _uiState.update { it.copy(allSettlementDetails = updatedDetails) }
                val nextIndex = currentState.currentSettlementIndex + 1
                if (nextIndex < currentState.settlementQueue.size) {
                    // 아직 정산할 통화가 남았다면, 다음 통화로 넘어갑니다.
                    _uiState.update { it.copy(currentSettlementIndex = nextIndex) }
                    prepareSettlementForCurrentCurrency() // 다음 통화 정산 준비
                } else {
                    // 모든 통화의 정산이 끝났으면, 인증 단계로 넘어갑니다.
                    if (isBiometricsEnabledByUser) {
                        _uiState.update { it.copy(currentStep = CalculateStep.BIOMETRIC) }
                        viewModelScope.launch { _navigationEvent.emit(CalculateNavEvent.ShowBiometricPrompt) }
                    } else {
                        _uiState.update { it.copy(currentStep = CalculateStep.PIN) }
                    }
                }
            }
            CalculateStep.BIOMETRIC -> skipBiometrics()
            CalculateStep.PIN -> {
                if (!_uiState.value.isPinLocked) {
                    checkPin()
                }
            }
            CalculateStep.FINISH -> {
                viewModelScope.launch {
                    _navigationEvent.emit(CalculateNavEvent.NavigateBack)
                }
            }
        }
    }

    fun onBackClick() {
        val currentStep = _uiState.value.currentStep
        if (currentStep == CalculateStep.CHOOSE_CURRENCY || currentStep == CalculateStep.FINISH) {
            viewModelScope.launch {
                _navigationEvent.emit(CalculateNavEvent.NavigateBack)
            }
        } else {
            val previousStep = when (currentStep) {
                CalculateStep.HOW_TO_CALCULATE -> CalculateStep.CHOOSE_CURRENCY
                CalculateStep.BIOMETRIC, CalculateStep.PIN -> CalculateStep.HOW_TO_CALCULATE
                else -> currentStep
            }
            _uiState.update { it.copy(currentStep = previousStep) }
        }
    }

    private fun prepareSettlementForCurrentCurrency() {
        val currentState = _uiState.value
        val boxInfo = currentState.boxInfo ?: return
        if (currentState.settlementQueue.isEmpty()) return

        val currentCurrency = currentState.settlementQueue[currentState.currentSettlementIndex]
        val totalAmount = boxInfo.balances.find { it.currency == currentCurrency }?.balance ?: 0.0

        // **핵심 수정: 원본 participants를 기준으로 전체 멤버 리스트를 유지**
        val selectedParticipants = currentState.participants.filter {
            currentState.selectedMemberIds.contains(it.id)
        }

        // --- 이 부분이 빠져있었습니다 ---
        val participantCount = selectedParticipants.size.takeIf { it > 0 } ?: 1
        val owner = selectedParticipants.find { it.permission.isOwner }
        val baseAmount = totalAmount / participantCount
        val remainder = totalAmount - (baseAmount * participantCount)

        // **모든 원본 멤버를 포함하되, 선택 여부에 따라 금액 다르게 설정**
        val settlementParticipants = currentState.participants.map { member ->
            if (currentState.selectedMemberIds.contains(member.id)) {
                // 선택된 멤버: 금액 계산
                val finalAmount = if (member.id == owner?.id) {
                    baseAmount + remainder
                } else {
                    baseAmount
                }
                SettlementParticipant(
                    member = member,
                    amount = finalAmount,
                    amountStr = finalAmount.toFormattedString(),
                    isManuallyEdited = false,
                    isEnabled = true
                )
            } else {
                // 선택되지 않은 멤버: 0원, 편집 불가
                SettlementParticipant(
                    member = member,
                    amount = 0.0,
                    amountStr = "0",
                    isManuallyEdited = false,
                    isEnabled = false
                )
            }
        }

        _uiState.update {
            it.copy(
                totalSettlementAmount = totalAmount,
                settlementParticipants = settlementParticipants
            )
        }
    }

    private fun submitSettlement() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) } // 로딩 시작

            val currentState = _uiState.value

            // 1. 저장된 모든 정산 데이터를 API 요청 DTO 형식으로 변환
            val settlementRequestList = currentState.allSettlementDetails.flatMap { (currency, participants) ->
                participants
                    .filter { it.amount > 0 } // 금액이 0원인 경우는 제외
                    .map { participant ->
                        SettlementRequestDto(
                            boxMemberId = participant.member.id, // BoxMember의 id가 boxMemberId라고 가정
                            amount = participant.amount,
                            currencyType = currency
                        )
                    }
            }

            // 2. UseCase를 통해 API 호출
            val result = settleBoxUseCase(boxId, settlementRequestList)

            result.onSuccess {
                // 성공 시, 로딩 종료 및 완료 화면으로 이동
                _uiState.update { it.copy(isSubmitting = false, currentStep = CalculateStep.FINISH) }
            }.onFailure { error ->
                // 실패 시, 로딩 종료 및 에러 메시지 표시 (Toast나 Dialog 등으로)
                _uiState.update { it.copy(isSubmitting = false, errorMessage = "정산 요청에 실패했습니다: ${error.message}") }
            }
        }
    }


    private fun Double.toFormattedString(): String {
        // #,##0.#### 패턴: 천 단위 콤마, 소수점은 있을 때만 최대 4자리까지 표시
        return DecimalFormat("#,##0.####").format(this)
    }
}