package com.d108.moyeo.presentation.ui.screen.currency.exchange

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.ui.component.KeypadKey
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.CustomKeypad
import com.d108.moyeo.presentation.ui.component.KeyMode

@Composable
fun PinContent(
    pin: String,
    pinError: String?,
    isPinLocked: Boolean,
    onKeyPress: (KeypadKey) -> Unit
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "PIN을 입력해주세요", style = Typography.titleLarge)
        Spacer(Modifier.weight(1f))
        PinDisplay(pinLength = pin.length)
        Box(
            modifier = Modifier.height(24.dp).padding(top = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (pinError != null) {
                Text(
                    text = pinError,
                    color = MaterialTheme.colorScheme.error,
                    style = Typography.bodySmall
                )
            }
        }
        Spacer(Modifier.weight(1f))
        if (isPinLocked) {
            Box(
                modifier = Modifier.fillMaxWidth().height(320.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("PIN이 잠겼습니다.\n나중에 다시 시도해주세요.", textAlign = TextAlign.Center)
            }
        } else {
            CustomKeypad(
                keypadColortype = "normal",
                keyMode = KeyMode.Reset,
                onKeyPress = onKeyPress,
                modifier = Modifier.padding(horizontal = Spacing.Large).height(320.dp)
            )
        }
    }
}

@Composable
private fun PinDisplay(pinLength: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(6) { index ->
            val isFilled = index < pinLength
            Box(
                modifier = Modifier
                    .size(Spacing.Large)
                    .background(
                        color = if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
            )
        }
    }
}