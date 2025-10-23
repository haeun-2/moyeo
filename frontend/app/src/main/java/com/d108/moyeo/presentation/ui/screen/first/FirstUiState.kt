package com.d108.moyeo.presentation.ui.screen.first

data class FirstUiState(
    val currentStep: FirstStep = FirstStep.BIOMETRICS,
    val pin: String = "",  // 6자리 핀번호 입력
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val pinError: String? = null
)
