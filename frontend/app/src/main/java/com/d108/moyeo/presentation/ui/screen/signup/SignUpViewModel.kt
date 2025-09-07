package com.d108.moyeo.presentation.ui.screen.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 회원가입 과정의 모든 상태를 담는 데이터 클래스
data class SignUpUiState(
    val currentStep: SignUpStep = SignUpStep.NAME,

    val name: String = "",  // 사용자 이름

    val accountBank: String = "",  // 은행 이름
    val accountNumber: String = "",  // 계좌번호

    val oneCoinNumber: String = "",  // 1원 인증으로 입력받을 번호
    val isOneCoinVerified: Boolean = false,  // 1원인증 완료 여부

    val isTermsAccepted: Boolean = false,  // 약관 동의 여부

    val pin: String = "",  // 6자리 핀번호 최초 입력
    val pinConfirm: String = "",  // 6자리 핀번호 확인

    val allTermsAccepted: Boolean = false,
    val termsOfServiceAccepted: Boolean = false,
    val privacyPolicyAccepted: Boolean = false,

    val isBiometricsUsed: Boolean = false // 생체인증 쓰는지 여부
)

// UI로 전달할 일회성 탐색 이벤트
sealed class SignUpNavigationEvent {
    object NavigateToHome : SignUpNavigationEvent()
    object NavigateBack : SignUpNavigationEvent() // 뒤로가기 이벤트 추가
}

class SignUpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    // 화면 이동과 같은 일회성 이벤트를 전달하기 위한 SharedFlow
    private val _navigationEvent = MutableSharedFlow<SignUpNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    val bankList: List<String> = listOf(
        "NH농협은행", "카카오뱅크", "KB국민은행", "신한은행", "토스뱅크", "우리은행", "IBK기업은행", "하나은행",
        "NH농협은행", "카카오뱅크", "KB국민은행", "신한은행", "토스뱅크", "우리은행", "IBK기업은행", "하나은행"
    )

    fun onNextClicked() {
        // 현재 단계에 따라 다음 단계로 상태를 변경하는 로직
        val nextStep = when (_uiState.value.currentStep) {  // before -> after
            SignUpStep.NAME -> SignUpStep.ACCOUNT
            SignUpStep.ACCOUNT -> SignUpStep.VERIFY_ACCOUNT
            SignUpStep.VERIFY_ACCOUNT -> SignUpStep.TERMS
            SignUpStep.TERMS -> SignUpStep.PIN
            SignUpStep.PIN -> SignUpStep.PIN_CONFIRM
            SignUpStep.PIN_CONFIRM -> SignUpStep.BIOMETRICS
            SignUpStep.BIOMETRICS -> SignUpStep.COMPLETE
            SignUpStep.COMPLETE -> null // 마지막 단계에서는 다른 동작 처리 (예: 홈으로 이동)
        }

        if (nextStep != null) {
            _uiState.update { it.copy(currentStep = nextStep) }
        } else {
            // TODO: 회원가입 완료 후 홈 화면으로 이동하는 로직
        }
    }

    fun onBackClicked() {
        val currentStep = _uiState.value.currentStep
        val previousStep = when (currentStep) {
            SignUpStep.ACCOUNT -> SignUpStep.NAME
            SignUpStep.VERIFY_ACCOUNT -> SignUpStep.ACCOUNT
            SignUpStep.TERMS -> SignUpStep.VERIFY_ACCOUNT
            SignUpStep.PIN -> SignUpStep.TERMS
            SignUpStep.PIN_CONFIRM -> SignUpStep.PIN
            SignUpStep.BIOMETRICS -> SignUpStep.PIN_CONFIRM
            SignUpStep.COMPLETE -> SignUpStep.BIOMETRICS
            else -> null // 첫 단계(NAME)에서는 이전 단계가 없음
        }

        if (previousStep != null) {
            // 이전 단계가 있으면 상태만 업데이트
            _uiState.update { it.copy(currentStep = previousStep) }
        } else {
            // 첫 단계에서 뒤로가기를 누르면, 화면을 닫으라는 이벤트를 발생시킴
            viewModelScope.launch {
                _navigationEvent.emit(SignUpNavigationEvent.NavigateBack)
            }
        }
    }

    fun onPinInput(digit: String, isConfirm: Boolean) {
        if (isConfirm) {
            if (_uiState.value.pinConfirm.length < 6) {
                _uiState.update { it.copy(pinConfirm = it.pinConfirm + digit) }
            }
        } else {
            if (_uiState.value.pin.length < 6) {
                _uiState.update { it.copy(pin = it.pin + digit) }
            }
        }
    }

    fun onPinBackspace(isConfirm: Boolean) {
        if (isConfirm) {
            _uiState.update { it.copy(pinConfirm = it.pinConfirm.dropLast(1)) }
        } else {
            _uiState.update { it.copy(pin = it.pin.dropLast(1)) }
        }
    }

    fun onPinClear(isConfirm: Boolean) {
        if (isConfirm) {
            _uiState.update { it.copy(pinConfirm = "") }
        } else {
            _uiState.update { it.copy(pin = "") }
        }
    }

    // 각 데이터 변경 시 호출될 함수들


    // 이름 단계
    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    // 계좌 단계
    fun onAccountBankChanged(accountBank: String) {
        _uiState.update { it.copy(accountBank = accountBank) }
    }

    fun onAccountNumberChanged(accountNumber: String) {
        _uiState.update { it.copy(accountNumber = accountNumber) }
    }

    // 1원 인증단계
    fun onOneCoinNumberChanged(oneCoinNumber: String) {
        _uiState.update { it.copy(oneCoinNumber = oneCoinNumber) }
    }

    fun onIsOneCoinVerifiedChanged(isOneCoinVerified: Boolean) {
        _uiState.update { it.copy(isOneCoinVerified = isOneCoinVerified) }
    }

    // 약관 동의 상태
    fun onAllTermsChanged(isChecked: Boolean) {
        _uiState.update {
            it.copy(
                allTermsAccepted = isChecked,
                termsOfServiceAccepted = isChecked,
                privacyPolicyAccepted = isChecked
            )
        }
    }

    fun onTermsOfServiceChanged(isChecked: Boolean) {
        _uiState.update {
            val allChecked = isChecked && it.privacyPolicyAccepted
            it.copy(termsOfServiceAccepted = isChecked, allTermsAccepted = allChecked)
        }
    }

    fun onPrivacyPolicyChanged(isChecked: Boolean) {
        _uiState.update {
            val allChecked = isChecked && it.termsOfServiceAccepted
            it.copy(privacyPolicyAccepted = isChecked, allTermsAccepted = allChecked)
        }
    }


    // 6자리 핀번호 입력
    fun onPinChanged(pin: String) {
        _uiState.update { it.copy(pin = pin) }
    }

    fun onPinConfirmChanged(pinConfirm: String) {
        _uiState.update { it.copy(pinConfirm = pinConfirm) }
    }



    fun onIsBiometricsUsedChanged(isBiometricsUsed: Boolean) {
        _uiState.update { it.copy(isBiometricsUsed = isBiometricsUsed) }
    }

}
