package com.d108.moyeo.presentation.ui.screen.exchange

import com.d108.moyeo.data.remote.dto.exchange.ExchangeRateItem

data class ExchangeUiState(
    val ratesList: List<ExchangeRateItem> = emptyList(),
    val selectedItem: ExchangeRateItem? = null,
    val isEditMode: Boolean = false,
    val showModal: Boolean = false,
    val draggedItem: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
