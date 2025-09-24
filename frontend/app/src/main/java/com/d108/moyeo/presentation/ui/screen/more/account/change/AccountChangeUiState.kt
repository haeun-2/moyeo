package com.d108.moyeo.presentation.ui.screen.more.account.change

data class AccountChangeUiState(
    val step: AccountChangeStep = AccountChangeStep.INPUT,
    val bankCode: String = "",
    val bankName: String = "",
    val bankAccount: String = "",
    val verificationCode: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)