package com.d108.moyeo.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.d108.moyeo.R

sealed class AppScreen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
    val iconResId: Int? = null
) {

    // ImageVector가 있으면 바텀 내비 클릭해서 보이는 화면
    // ImageVector가 없으면 스크린

    object Dummy: AppScreen(route = "dummy", title = "Dummy")

    // 홈 스크린 관련
    object Home: AppScreen(route = "home", title = "홈", icon = Icons.Default.Home)
    object HomeSecond : AppScreen(route = "home_second", title = "Home Second") // HomeSecond 스크린 추가


    // 환율 스크린 관련
    object Exchange: AppScreen(route = "exchange", title = "환율", iconResId = R.drawable.outline_currency_exchange_24)



    // QR 스크린 관련
    object QR: AppScreen(route = "qr", title = "QR", iconResId = R.drawable.outline_qr_code_24)



    // 통장 히스토리 관련
    object History: AppScreen(route = "history", title = "기록", icon = Icons.Default.AccountBox)


    // 삼점바 관련
    object More: AppScreen(route = "more", title = "메뉴", iconResId = R.drawable.outline_more_horiz_24)
    object ConnectedAccountSettings : AppScreen(route = "connected_account_settings", title = "연결 계좌 설정")
    object Notices : AppScreen(route = "notices", title = "공지사항")
    object ChangePassword : AppScreen(route = "change_password", title = "비밀번호 변경")
    object FAQ: AppScreen(route = "faq", title = "자주 묻는 질문")

}