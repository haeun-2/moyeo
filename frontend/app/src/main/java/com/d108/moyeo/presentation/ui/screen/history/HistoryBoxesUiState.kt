package com.d108.moyeo.presentation.ui.screen.history

import com.d108.moyeo.domain.model.box.Box

data class HistoryBoxesUiState(
    val allBoxes: List<Box> = emptyList(),
    val newlySelectedBoxId: Long? = null, // '새로 선택된' 박스 ID를 기억할 상태
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)