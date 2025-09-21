package com.d108.moyeo.presentation.ui.component.home

object FilterOptionData {
    val allScopeOptions = listOf(
        "전체", "입금", "출금", "환전", "식사", "교통", "숙박", "투어/액티비티", "쇼핑", "기타"
    )

    val scopePages = listOf(
        listOf("전체"),
        listOf("입금", "출금", "환전"),
        listOf("식사", "교통", "숙박"),
        listOf("투어/액티비티", "쇼핑", "기타")
    )
}