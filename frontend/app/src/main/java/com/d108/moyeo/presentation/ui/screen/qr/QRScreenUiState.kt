package com.d108.moyeo.presentation.ui.screen.qr

import androidx.compose.ui.graphics.ImageBitmap
import com.d108.moyeo.domain.model.box.Box

data class QRScreenUiState(
    val bookmarkedBoxes: List<Box> = emptyList(),

    val selectedBoxId: Long? = null,
    val isLoadingBoxes: Boolean = false,

    val isLoadingQR: Boolean = false,    // QR 코드 로딩 상태
    val qrImageBitmap: ImageBitmap? = null, // QR 비트맵 이미지

    val timerText: String = "00:30", // 타이머 표시를 위한 상태
    val isTimerRunning: Boolean = false, // 타이머 동작 여부를 위한 상태
    val errorMessage: String? = null

)
