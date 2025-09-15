package com.d108.moyeo.presentation.ui.screen.signup

enum class SignUpStep {
    NAME,           // 1. 이름 입력

    EMAIL_INPUT,  // 이메일 입력
    EMAIL_VERIFY,  // 이메일 인증

    PHONE_INPUT,    // 전화번호 입력
    PHONE_VERIFY,   // 전화번호 인증

    ACCOUNT,        // 계좌번호 입력
    ACCOUNT_VERIFY, //  1원 인증

    TERMS,          // 약관 동의

    PIN,            // 6자리 PIN 입력
    PIN_CONFIRM,

    BIOMETRICS,     // 6. 생체 인증 사용 여부
    COMPLETE        // 7. 가입 완료
}
