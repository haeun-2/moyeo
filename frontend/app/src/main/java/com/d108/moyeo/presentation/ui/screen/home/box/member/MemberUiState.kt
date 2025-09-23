package com.d108.moyeo.presentation.ui.screen.home.box.member

data class MemberCapabilities(
    val settleAllowed: Boolean,
    val payAllowed: Boolean,
    val fxAllowed: Boolean
)

data class BoxMemberUi(
    val id: Long,
    val name: String,
    val isOwner: Boolean,
    val capabilities: MemberCapabilities,
    val expanded: Boolean = false
)

data class MemberUiState(
    val isLoading: Boolean = false,
    val members: List<BoxMemberUi> = emptyList(),
    val error: String? = null
)
