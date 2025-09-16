package com.d108.moyeo.presentation.ui.screen.qr

import com.d108.moyeo.domain.model.box.Box

data class QrScreenUiState(
    val bookmarkedBoxes: List<Box> = emptyList(),
    val selectedBoxId: Long? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
    // TODO: 나중에 QR 코드 Bitmap, 토큰 등의 상태를 추가해야 합니다.
)
