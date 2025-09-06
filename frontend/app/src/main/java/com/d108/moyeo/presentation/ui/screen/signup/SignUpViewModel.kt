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
    val isOneCoinVerified: Boolean = false,  // 1원인증 완료 여부
    val isTermsAccepted: Boolean = false,  // 약관 동의 여부
    val pin: String = "",  // 6자리 핀번호
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
    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }
    // ... 계좌번호 등 다른 데이터 변경 함수들 ...
}
