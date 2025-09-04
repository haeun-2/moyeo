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
    object Dummy: AppScreen(route = "dummy", title = "Dummy")
    object Home: AppScreen(route = "home", title = "Home", icon = Icons.Default.Home)
    object Exchange: AppScreen(route = "exchange", title = "Exchange", icon = Icons.Default.Call)
    object QR: AppScreen(route = "qr", title = "QR", icon = Icons.Default.Favorite)
    object History: AppScreen(route = "history", title = "History", icon = Icons.Default.AccountBox)
    object More: AppScreen(route = "more", title = "More", icon = Icons.Default.Build)

    // ImageVector가 없으면 스크린
}