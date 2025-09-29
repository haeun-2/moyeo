package com.d108.moyeo.presentation.ui.screen.home.join

data class JoinUiState(
    val currentStep: JoinStep = JoinStep.BOX,
    val joinCode: String = "",
    val biometricsEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
