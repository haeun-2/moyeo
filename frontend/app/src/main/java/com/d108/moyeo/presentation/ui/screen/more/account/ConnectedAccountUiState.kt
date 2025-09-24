package com.d108.moyeo.presentation.ui.screen.more.account

import com.d108.moyeo.domain.model.ConnectedAccount

data class ConnectedAccountUiState(
    val isLoading: Boolean = false,
    val account: ConnectedAccount? = null,
    val error: String? = null
)