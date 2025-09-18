package com.d108.moyeo.presentation.ui.screen.home.box.collecting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun BiometricContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "생체 인증을\n완료해주세요!",
            style = Typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
}