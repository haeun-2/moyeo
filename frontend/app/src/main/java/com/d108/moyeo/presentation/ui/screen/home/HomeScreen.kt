package com.d108.moyeo.presentation.ui.screen.home

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.d108.moyeo.presentation.navigation.AppScreen

@Composable
fun HomeScreen(navController: NavController) {
    Text("홈 스크린")
    Button(onClick = {
        navController.navigate(AppScreen.HomeSecond.route) // HomeSecondScreen으로 이동
    }) {
        Text("두 번째 홈 스크린으로 이동 (바텀 내비 숨김)")
    }
}