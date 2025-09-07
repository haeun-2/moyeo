package com.d108.moyeo.presentation.ui.screen.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun BiometricsContent(viewModel: SignUpViewModel) {
    Column {
        Text("생체인증을\n사용하시겠어요?", style = Typography.titleLarge)

        Spacer(Modifier.height(20.dp))  // 후에 상수화 할 것


        // 지문 이미지가 있는 어떤 영역. 남은 공간을 다 차지함
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text("지문 이미지 영역")
        }
    }
}