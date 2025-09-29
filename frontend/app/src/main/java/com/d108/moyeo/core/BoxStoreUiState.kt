package com.d108.moyeo.core

import androidx.compose.ui.graphics.Color
import com.d108.moyeo.domain.model.box.Balance
import com.d108.moyeo.domain.model.box.BoxType

data class BoxStoreUiState(
    val type: BoxType,
    val id: Long,
    val title: String,  // 박스 이름 -> 어디서나 필요
    val bg: Color,  // 박스 배경 색 -> 어디서나 필요
    val textColor: Color,  // 박스 글자 색 -> 어디서나 필요
    val isBookmarked: Boolean,  // 즐겨찾기 정보 -> 어디서나 필요
    val amount: String, // 대표 화폐 -> HomeScreen에서 필요
    val balances: List<Balance> // 모든 화폐 목록 -> QRScreen에서만 필요
)