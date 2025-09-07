package com.d108.moyeo.presentation.ui.screen.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun CompleteContent() {
    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = Padding.VerticalLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "모든 인증을 완료했어요",
            style = Typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Text(
            text = "🎉",
            fontSize = 80.sp
        )

        Text(
            text = "모여 박스로 돈을 모아\n해외 여행을 떠나요",
            style = Typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}