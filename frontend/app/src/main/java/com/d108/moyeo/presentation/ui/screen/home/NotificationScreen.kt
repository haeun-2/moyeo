package com.d108.moyeo.presentation.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun NotificationScreen(navController: NavController) {
    Column(
        modifier = Modifier.padding(
            start = Spacing.Medium, end = Spacing.Medium,
            top = Padding.ScreenTop, bottom = Padding.ScreenBottom
        ),
    ) {
        // 제목
        Box(
            modifier = Modifier.fillMaxWidth(), // 가로 전체 차지
            contentAlignment = Alignment.Center // 중앙 정렬
        ) {
            Text(
                text = "알림",
                style = Typography.titleLarge
            )
        }
    }
}