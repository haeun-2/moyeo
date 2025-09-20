package com.d108.moyeo.presentation.ui.screen.home.box.collecting

data class CollectingUiState(
    val currentStep: CollectingStep = CollectingStep.CHOOSE_CURRENCY,
    val boxId: String = "", // 어떤 박스에 모을지
    val currency: String = "", // 어떤 화폐를 모을지
    val howMuch: String = "",
    val pin: String = "",

    // PIN 검증을 위한 상태
    val pinFailureCount: Int = 0,
    val isPinLocked: Boolean = false,
    val pinError: String? = null,

    // 생체 인증 활성화 여부 확인
    val biometricsEnabled: Boolean = false
)