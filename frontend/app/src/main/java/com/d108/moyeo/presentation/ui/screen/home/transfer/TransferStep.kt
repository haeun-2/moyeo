package com.d108.moyeo.presentation.ui.screen.home.transfer

enum class TransferStep {  // 이체 화면 순서
    TARGET_BOX,  // 어느 박스로 보내나요?

    CHOOSE_CURRENCY,  // 어떤 금액을 보내나요?

    HOW_MUCH,  // 보낼 금액을 입력해주세요


    // 사용자가 지문 활성화 하면
    BIOMETRIC, // 지문 인증 시도

    // 지문 활성화하지 않거나 지문인식에 실패하여 건너뛰려면
    PIN, // 핀 인증 시도

    FINISH  // 축하드립니다!
}