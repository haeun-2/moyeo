package com.d108.moyeo.presentation.ui.screen.signup

enum class SignUpStep {
    NAME,           // 1. 이름 입력
    ACCOUNT,        // 2. 계좌번호 입력
    VERIFY_ACCOUNT, // 3. 1원 인증
    TERMS,          // 4. 약관 동의
    PIN,            // 5. 6자리 PIN 입력
    PIN_CONFIRM,

    BIOMETRICS,     // 6. 생체 인증 사용 여부
    COMPLETE        // 7. 가입 완료
}
