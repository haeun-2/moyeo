package com.d108.moyeo.presentation.ui.screen.currency.reservation

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
fun PinContent(viewModel: ReservationViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("PIN을 입력해주세요", style = Typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.weight(1f))

        // 점 표시
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
            repeat(6) { idx ->
                val filled = idx < uiState.pin.length
                Box(
                    modifier = Modifier
                        .size(Spacing.Large)
                        .background(color = if (filled) primaryLight else Color.Gray.copy(alpha = 0.3f), shape = CircleShape)
                )
            }
        }

        Box(
            modifier = Modifier.height(24.dp).padding(top = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            uiState.pinError?.let { Text(it, color = errorLight, style = Typography.bodySmall) }
        }

        Spacer(Modifier.weight(1f))

        if (uiState.isPinLocked) {
            Box(Modifier.fillMaxWidth().height(280.dp), contentAlignment = Alignment.Center) {
                Text("PIN이 잠겼습니다.\n나중에 다시 시도해주세요.", textAlign = TextAlign.Center)
            }
        } else {
            CustomKeypad(
                keypadColortype = "normal",
                keyMode = KeyMode.Reset,
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
