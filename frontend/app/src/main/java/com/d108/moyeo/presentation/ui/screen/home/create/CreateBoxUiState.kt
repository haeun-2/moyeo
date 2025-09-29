package com.d108.moyeo.presentation.ui.screen.home.create

data class CreateBoxUiState(
    val name: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val createdBoxId: Long? = null,
    val currentStep: CreateBoxStep = CreateBoxStep.NAME,

    // 초대 링크 정보
    val inviteLink: String? = null,
    val inviteCode: String? = null,
    val expiresAt: String? = null
)