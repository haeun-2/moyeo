package com.d108.moyeo.presentation.ui.screen.home.wallet

import com.d108.moyeo.domain.model.history.HistoryTransaction

data class MyWalletDetailUiState(
    val transaction: HistoryTransaction? = null,


    val isMemoEditing: Boolean = false,
    val isCategoryEditing: Boolean = false,

    val editedMemo: String = "",
    val selectedCategoryId: Long? = null,

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)