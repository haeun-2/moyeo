package com.d108.moyeo.presentation.ui.screen.home.join

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.CustomKeypad
import com.d108.moyeo.presentation.ui.component.KeypadKey

@Composable
fun PinJoinContent(
    pinLength: Int,
    errorMessage: String?,
    onDigit: (Int) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text("PIN 번호를 입력해주세요", style = Typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.weight(1f))

        // 6자리 PIN 점 표시
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(6) { index ->
                val filled = index < pinLength
                Box(
                    modifier = Modifier
                        .size(Spacing.Large)
                        .then(
                            Modifier.background(
                                color = if (filled) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                        )
                )
            }
        }

        // 오류 메시지
        Box(
            modifier = Modifier.height(Spacing.Large).padding(top = Spacing.Small),
            contentAlignment = Alignment.Center
        ) {
            if (errorMessage != null) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error, style = Typography.bodySmall)
            }
        }

        Spacer(Modifier.weight(1f))

        CustomKeypad(
            onKeyPress = { key ->
                when (key) {
                    is KeypadKey.Digit -> onDigit(key.value)
                    KeypadKey.Clear -> onClear()
                    KeypadKey.Backspace -> onBackspace()
                    else -> {}
                }
            },
            keypadColortype = "normal"
        )
    }
}
