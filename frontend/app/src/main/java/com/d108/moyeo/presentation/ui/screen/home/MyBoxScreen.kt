package com.d108.moyeo.presentation.ui.screen.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun MyBoxScreen(navController: NavController, boxId: String) {
    Text("마이 박스 스크린, 받은 아이디: $boxId")
}