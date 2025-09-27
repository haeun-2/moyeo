package com.d108.moyeo.presentation.ui.screen.currency

import com.d108.moyeo.data.remote.dto.exchange.CurrencyResponseDto

data class CurrencyUiState(
    val ratesList: List<CurrencyResponseDto> = emptyList(),
    val selectedItem: CurrencyResponseDto? = null,
    val isEditMode: Boolean = false,
    val showModal: Boolean = false,
    val draggedItem: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
