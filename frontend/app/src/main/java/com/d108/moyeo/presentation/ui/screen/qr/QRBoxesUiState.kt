package com.d108.moyeo.presentation.ui.screen.qr

import com.d108.moyeo.domain.model.box.Box

data class QRBoxesUiState(
    val allBoxes: List<Box> = emptyList(),

    val newlySelectedBoxId: Long? = null,  // 새롭게 북마크할 박스 아이디
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
