package com.d108.moyeo.presentation.ui.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.errorLight
import com.d108.moyeo.presentation.ui.component.CustomKeypad
import com.d108.moyeo.presentation.ui.component.KeypadKey

@Composable
fun PinLoginContent(viewModel: LoginViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("PIN 번호를 입력해주세요", style = Typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.weight(1f))

        // PIN 입력 상태를 보여주는 6개의 점
        PinDisplay(pinLength = uiState.pin.length)

        // 에러 메시지 공간 확보
        Box(
            modifier = Modifier
                .height(Spacing.Large)
                .padding(top = Spacing.Small),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = Typography.bodySmall
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // 재사용 가능한 커스텀 키패드
        CustomKeypad(
            onKeyPress = { key ->
                when (key) {
                    is KeypadKey.Digit -> viewModel.onPinDigitInput(key.value.toString())
                    KeypadKey.Clear -> viewModel.onPinClear()
                    KeypadKey.Backspace -> viewModel.onPinBackspace()
                    else -> {}
                }
            },
            keypadType = "normal"
        )
    }
}

// PIN 입력 상태를 보여주는 6개의 점 (UI Helper)
@Composable
private fun PinDisplay(pinLength: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(6) { index ->
            val isFilled = index < pinLength
            Box(
                modifier = Modifier
                    .size(Spacing.Large)
                    .background(
                        color = if (isFilled) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            )
        }
    }
}