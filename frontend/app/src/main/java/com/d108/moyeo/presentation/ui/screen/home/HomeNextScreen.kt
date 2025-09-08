package com.d108.moyeo.presentation.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun HomeSecondScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("여기는 홈의 두 번째 스크린입니다.")
        Button(onClick = { navController.popBackStack() }) { // 뒤로 가기 버튼 (예시)
            Text("뒤로 가기")
        }
    }
}