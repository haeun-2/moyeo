package com.d108.moyeo.presentation.ui.screen.home.box.member

import com.d108.moyeo.domain.model.box.BoxMember
import com.d108.moyeo.domain.model.box.Permission

data class BoxMemberUi(
    val member: BoxMember,
    val expanded: Boolean = false //
)

data class MemberUiState(
    val isLoading: Boolean = false,
    val members: List<BoxMemberUi> = emptyList(), // [수정] BoxMemberUi의 리스트를 가짐
    val error: String? = null
)
