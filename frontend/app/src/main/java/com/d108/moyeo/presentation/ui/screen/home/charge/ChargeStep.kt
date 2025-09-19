package com.d108.moyeo.presentation.ui.screen.home.charge

enum class ChargeStep {
    HOW_MUCH, // 얼마를 충전하나요
    BIOMETRIC, // 지문 인증 시도

    // 지문 활성화하지 않거나 지문인식에 실패하여 건너뛰려면
    PIN, // 핀 인증 시도
    FINISH // 끝
}