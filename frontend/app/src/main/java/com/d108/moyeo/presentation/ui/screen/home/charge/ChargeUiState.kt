package com.d108.moyeo.presentation.ui.screen.home.charge

data class ChargeUiState(
    val currentStep: ChargeStep = ChargeStep.HOW_MUCH,
    val howMuch: String = "",
    val pin: String = "",

    // PIN 검증을 위한 상태
    val pinFailureCount: Int = 0,
    val isPinLocked: Boolean = false,
    val pinError: String? = null,

    // 생체 인증 활성화 여부 확인
    val biometricsEnabled: Boolean = false
)