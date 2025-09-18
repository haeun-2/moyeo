package com.d108.moyeo.domain.model.box

data class Box(
    val id: Long,
    val name: String,
    val balances: List<Balance>,
    val type: BoxType,
    val isBookmarked: Boolean = false  // 개인박스는 도메인(앱) 레벨에서 항상 false일 것
)