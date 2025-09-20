package com.d108.moyeo.presentation.ui.screen.home

import androidx.compose.ui.graphics.Color

/** 상단 지갑 요약 */
data class WalletSummary(
    val id: Long,
    val title: String,
    val bg: Color,
    val balances: List<CurrencyBalance>
)

/** 통화 한 줄 */
data class CurrencyBalance(
    val label: String,  // "한국 원", "미국 달러" 등
    val value: String,  // "120,000 KRW" 등 표시용
    val code: String    // "KRW", "USD" ...
)

/** 홈의 그룹(모임) 박스 카드 */
data class GroupBox(
    val id: Long,
    val title: String,
    val amount: String, // 대표 금액 문자열
    val bg: Color,       // 카드 배경색
    val isBookmarked: Boolean
)

/** HomeScreen 전체 상태 */
data class HomeUiState(
    val wallet: WalletSummary,
    val groups: List<GroupBox> = emptyList(),
    val showWalletEditSheet: Boolean = false,
    val editingGroupId: Long? = null,
    val showGroupEditSheet: Boolean = false,
    val isRefreshing: Boolean = false
)