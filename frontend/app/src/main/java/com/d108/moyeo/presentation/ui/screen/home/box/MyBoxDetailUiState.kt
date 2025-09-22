package com.d108.moyeo.presentation.ui.screen.home.box

import com.d108.moyeo.domain.model.history.ExchangeHistoryDetail
import com.d108.moyeo.domain.model.history.HistoryTransaction

data class MyBoxDetailUiState(
    val transaction: HistoryTransaction? = null,

    val exchangeDetails: List<ExchangeHistoryDetail> = emptyList(),


    val isMemoEditing: Boolean = false,
    val editedMemo: String = "",

    val isCategoryEditing: Boolean = false,
    val selectedCategory: String = "",
    val showCategorySheet: Boolean = false,

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
