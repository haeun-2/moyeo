package com.d108.moyeo.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppScreen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {

    // ImageVector가 있으면 바텀 내비 클릭해서 보이는 화면
    // ImageVector가 없으면 스크린

    object Dummy: AppScreen(route = "dummy", title = "Dummy")


    // 로그인 관련
    object Login: AppScreen(route = "login", title= "Login")

    // 홈 스크린 관련
    object Home: AppScreen(route = "home", title = "Home", icon = Icons.Default.Home)
    object HomeSecond : AppScreen(route = "home_second", title = "Home Second") // HomeSecond 스크린 추가


    // 환율 스크린 관련
    object Exchange: AppScreen(route = "exchange", title = "Exchange", icon = Icons.Default.Call)



    // QR 스크린 관련
    object QR: AppScreen(route = "qr", title = "QR", icon = Icons.Default.Favorite)



    // 통장 히스토리 관련
    object History: AppScreen(route = "history", title = "History", icon = Icons.Default.AccountBox)


    // 삼점바 관련
    object More: AppScreen(route = "more", title = "More", icon = Icons.Default.Build)

}