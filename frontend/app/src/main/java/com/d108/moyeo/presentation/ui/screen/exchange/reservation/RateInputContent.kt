package com.d108.moyeo.presentation.ui.screen.exchange.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.ui.component.CustomKeypad
import com.d108.moyeo.presentation.ui.component.KeyMode
import com.d108.moyeo.presentation.ui.component.KeypadKey

@Composable
fun RateInputContent(
    paddingValues: PaddingValues,
    currencyCode: String,
    currencyName: String,
    input: String,
    currentRate: String,
    onInputChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(20.dp)
    ) {
        Text("예약하고 싶은 환율을\n확인해주세요", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("$currencyName ($currencyCode) • 기준: KRW", color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (currentRate.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text("현재 환율: $currentRate", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(Modifier.height(40.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Text(if (input == "0") "1" else input, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        }

        CustomKeypad(
            onKeyPress = { key ->
                when (key) {
                    is KeypadKey.Digit -> {
                        val next = if (input == "0") "${key.value}" else input + key.value
                        onInputChange(next)
                    }
                    KeypadKey.Clear -> { // Zeros 모드에서 "00"
                        val next = if (input == "0") "0" else input + "00"
                        onInputChange(next)
                    }
                    KeypadKey.Backspace -> {
                        val next = input.dropLast(1).ifBlank { "0" }
                        onInputChange(next)
                    }
                    else -> Unit
                }
            },
            keypadColortype = "normal",
            keyMode = KeyMode.Zeros,
            buttonAspectRatio = 1.2f
        )

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onNext,
            enabled = input != "0",
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) { Text("다음으로") }
    }
}
