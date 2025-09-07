package com.d108.moyeo.presentation.ui.screen.signup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun BiometricsContent(viewModel: SignUpViewModel) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("생체 인증을 사용하시겠어요?", style = Typography.titleLarge, textAlign = TextAlign.Center)
        Text("더욱 빠르고 안전하게 로그인할 수 있습니다.", style = Typography.bodyMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))
        Row {
            OutlinedButton(onClick = viewModel::onNextClicked, modifier = Modifier.weight(1f)) { Text("건너뛰기") }
            Spacer(Modifier.width(16.dp))
            Button(onClick = viewModel::onNextClicked,
                modifier = Modifier.weight(1f)
            ) {
                Text("사용하기")
            }
        }
    }
}