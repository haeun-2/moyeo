package com.d108.moyeo.core

import androidx.compose.ui.graphics.Color

data class BoxStoreUiState(
    val id: Long,
    val title: String,
    val bg: Color,
    val textColor: Color,
    val isBookmarked: Boolean = false
)