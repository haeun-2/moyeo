package com.d108.moyeo.presentation.ui.screen.home.transfer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.d108.moyeo.presentation.theme.primaryLight
import com.d108.moyeo.presentation.ui.component.CustomKeypad
import com.d108.moyeo.presentation.ui.component.KeyMode
import com.d108.moyeo.presentation.ui.component.KeypadKey

@Composable
fun PinContent(viewModel: SendingViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "PIN을 입력해주세요",
            style = Typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.weight(1f))

        // PIN 입력 상태를 보여주는 6개의 점
        PinDisplay(pinLength = uiState.pin.length)

        // 에러 메시지 영역
        Box(
            modifier = Modifier
                .height(24.dp) // 에러 메시지를 위한 고정 높이
                .padding(top = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.pinError != null) {
                Text(
                    text = uiState.pinError!!,
                    color = errorLight,
                    style = Typography.bodySmall
                )
            }
        }

        Spacer(Modifier.weight(1f))



        if (uiState.isPinLocked) {
            // TODO: "나중에 다시 시도해주세요" 대신, ViewModel로부터 남은 잠금 시간을 받아와
            //  - "5분 후에 다시 시도해주세요 (4:59)" 와 같이
            //  - 1초마다 줄어드는 타이머를 표시하도록 개선해야 합니다.
            Box(
                modifier = Modifier.fillMaxWidth().height(280.dp), // Keypad와 비슷한 높이
                contentAlignment = Alignment.Center
            ) {
                Text("PIN이 잠겼습니다.\n나중에 다시 시도해주세요.", textAlign = TextAlign.Center)
            }
        } else {
            // 재사용 가능한 커스텀 키패드
            CustomKeypad(
                keypadColortype = "normal",
                keyMode = KeyMode.Reset, // 금액 입력이 아니므로 '초기화' 모드
                onKeyPress = { key ->
                    when (key) {
                        is KeypadKey.Digit -> viewModel.onPinInput(key.value.toString())
                        KeypadKey.Clear -> viewModel.onPinClear()
                        KeypadKey.Backspace -> viewModel.onPinBackspace()
                        else -> {}
                    }
                },
            )
        }
    }
}


// PIN 입력 상태를 보여주는 6개의 점 (UI Helper)
@Composable
private fun PinDisplay(pinLength: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
        repeat(6) { index ->
            val isFilled = index < pinLength
            Box(
                modifier = Modifier
                    .size(Spacing.Large)
                    .background(
                        color = if (isFilled) primaryLight else Color.Gray.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            )
        }
    }
}