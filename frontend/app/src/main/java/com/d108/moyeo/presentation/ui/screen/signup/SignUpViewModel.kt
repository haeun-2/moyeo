package com.d108.moyeo.presentation.ui.screen.signup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

class SignUpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    fun onNextClicked() {
        // 현재 단계에 따라 다음 단계로 상태를 변경하는 로직
        val nextStep = when (_uiState.value.currentStep) {  // before -> after
            SignUpStep.NAME -> SignUpStep.ACCOUNT
            SignUpStep.ACCOUNT -> SignUpStep.VERIFY_ACCOUNT
            SignUpStep.VERIFY_ACCOUNT -> SignUpStep.TERMS
            SignUpStep.TERMS -> SignUpStep.PIN
            SignUpStep.PIN -> SignUpStep.BIOMETRICS
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
        // 뒤로가기 로직 (필요 시 구현)
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
