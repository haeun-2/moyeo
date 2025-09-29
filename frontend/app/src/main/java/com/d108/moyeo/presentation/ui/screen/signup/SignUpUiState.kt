package com.d108.moyeo.presentation.ui.screen.signup

import com.d108.moyeo.domain.model.Bank

data class SignUpUiState(
    val currentStep: SignUpStep = SignUpStep.NAME,
    val sessionId: String? = null, // 서버와 통신하기 위한 세션 ID 추가

    val name: String = "",  // 사용자 이름

    val email: String = "",  // 사용자 이메일
    val emailCode: String = "", // 이메일 코드
    val isEmailVerified: Boolean = false,  // 이메일 인증 여부

    val phoneNumber: String = "",  // 전화번호
    val phoneCode: String = "",
    val isPhoneNumberVerified: Boolean = false,  // 전화번호 인증 여부

    val bankList: List<Bank> = emptyList(),
    val showBankBottomSheet: Boolean = false,
    val accountBank: Bank? = null,
    val accountNumber: String = "",  // 계좌번호

    val oneCoinNumber: String = "",  // 1원 인증으로 입력받을 번호
    val isOneCoinVerified: Boolean = false,  // 1원인증 완료 여부

    val isTermsAccepted: Boolean = false,  // 약관 동의 여부

    val pin: String = "",  // 6자리 핀번호 최초 입력
    val pinConfirm: String = "",  // 6자리 핀번호 확인

    val allTermsAccepted: Boolean = false,
    val termsOfServiceAccepted: Boolean = false,
    val privacyPolicyAccepted: Boolean = false,

    val isBiometricsUsed: Boolean = false, // 생체인증 쓰는지 여부

    val isLoading: Boolean = false,
    val errorMessage: String? = null // 에러 메시지 상태
)
