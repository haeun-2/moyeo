package com.d108.moyeo.presentation.ui.screen.exchange

import com.d108.moyeo.presentation.ui.component.exchange.ExchangeRateData

data class ExchangeUiState(
    val ratesList: List<ExchangeRateData> = emptyList(),
    val isEditMode: Boolean = false,
    val showModal: Boolean = false,
    val draggedItem: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
