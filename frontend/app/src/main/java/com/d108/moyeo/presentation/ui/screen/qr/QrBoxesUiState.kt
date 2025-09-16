package com.d108.moyeo.presentation.ui.screen.qr

import com.d108.moyeo.domain.model.box.Box

data class QrBoxesUiState(
    val allBoxes: List<Box> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
