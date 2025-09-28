package com.d108.moyeo.presentation.ui.screen.currency.exchange

enum class ExchangeStep {
    TARGET_BOX,       // (CHARGE) 박스 선택
    CHOOSE_CURRENCY,  // (CHARGE) 지출 통화 선택
    HOW_MUCH,         // 금액 입력
    BIOMETRIC,        // ✨ 생체 인증
    PIN,              // ✨ PIN 인증
    FINISH            // 완료
}
