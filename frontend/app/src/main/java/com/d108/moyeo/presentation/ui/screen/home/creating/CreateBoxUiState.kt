package com.d108.moyeo.presentation.ui.screen.home.creating

data class CreateBoxUiState(
    val name: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val createdBoxId: Long? = null,
    val currentStep: CreateBoxStep = CreateBoxStep.NAME
)