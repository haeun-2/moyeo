package com.d108.moyeo.presentation.ui.screen.signup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun PinInputContent(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    PinContentLayout(
        title = "사용하실 PIN 6자리를 입력해주세요.",
        pinValue = uiState.pin,
        onDigitClick = { digit -> viewModel.onPinInput(digit, isConfirm = false) },
        onClearClick = { viewModel.onPinClear(isConfirm = false) },
        onBackspaceClick = { viewModel.onPinBackspace(isConfirm = false) }
    )
}

@Composable
fun PinConfirmContent(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    PinContentLayout(
        title = "PIN을 한번 더 입력해주세요.",
        pinValue = uiState.pinConfirm,
        onDigitClick = { digit -> viewModel.onPinInput(digit, isConfirm = true) },
        onClearClick = { viewModel.onPinClear(isConfirm = true) },
        onBackspaceClick = { viewModel.onPinBackspace(isConfirm = true) },
        errorMessage = if (uiState.pinConfirm.length == 6 && uiState.pin != uiState.pinConfirm) "PIN이 일치하지 않습니다." else null
    )
}

// PIN 입력/확인 화면의 공통 레이아웃
@Composable
private fun PinContentLayout(
    title: String,
    pinValue: String,
    onDigitClick: (String) -> Unit,
    onClearClick: () -> Unit,
    onBackspaceClick: () -> Unit,
    errorMessage: String? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(title, style = Typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))

        // 네모칸 6개
        PinDisplay(pinLength = pinValue.length)

        if (errorMessage != null) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error, style = Typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.weight(1f))

        // 커스텀 키패드
        PinKeypad(
            onDigitClick = onDigitClick,
            onClearClick = onClearClick,
            onBackspaceClick = onBackspaceClick
        )
    }
}

// PIN 입력 상태를 보여주는 6개의 점
@Composable
private fun PinDisplay(pinLength: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(6) { index ->
            val isFilled = index < pinLength
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = if (isFilled) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            )
        }
    }
}

// 4x3 커스텀 키패드
@Composable
private fun PinKeypad(
    onDigitClick: (String) -> Unit,
    onClearClick: () -> Unit,
    onBackspaceClick: () -> Unit
) {
    val buttons = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "초기화", "0", "←"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        items(buttons) { key ->
            TextButton(
                onClick = {
                    when (key) {
                        "초기화" -> onClearClick()
                        "←" -> onBackspaceClick()
                        else -> onDigitClick(key)
                    }
                },
                modifier = Modifier.aspectRatio(1.5f),
                shape = CircleShape,
                border = if (key == "초기화" || key == "←") null else BorderStroke(1.dp, Color.LightGray)
            ) {
                Text(key, style = Typography.headlineMedium)
            }
        }
    }
}