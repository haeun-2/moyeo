package com.d108.moyeo.presentation.ui.screen.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.errorLight
import com.d108.moyeo.presentation.ui.component.CustomKeypad
import com.d108.moyeo.presentation.ui.component.KeyMode
import com.d108.moyeo.presentation.ui.component.KeypadKey

@Composable
fun PinInputContent(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    PinContentLayout(
        title = "사용하실 PIN 6자리를 \n 입력해주세요.",
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
    var keyMode by remember { mutableStateOf(KeyMode.Zeros) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(title, style = Typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.weight(1f))

        // 네모칸 6개
        PinDisplay(pinLength = pinValue.length)

        if (errorMessage != null) {
            Text(
                errorMessage,
                color = errorLight,
                style = Typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        CustomKeypad(
            onKeyPress = { key ->
                when (key) {
                    is KeypadKey.Digit -> onDigitClick(key.value.toString())
                    KeypadKey.Clear -> when (keyMode) {
                        KeyMode.Reset -> onClearClick()
                        KeyMode.Zeros -> onDigitClick("00")
                    }
                    KeypadKey.Backspace -> onBackspaceClick()
                    is KeypadKey.Custom -> {}
                }
            },
            keypadType = "normal",
            keyMode = keyMode
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