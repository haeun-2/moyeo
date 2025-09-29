package com.d108.moyeo.presentation.ui.screen.currency.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.CustomKeypad
import com.d108.moyeo.presentation.ui.component.KeyMode
import com.d108.moyeo.presentation.ui.component.KeypadKey
import com.d108.moyeo.util.currencyUnitMap
import java.text.NumberFormat
import java.util.Locale

@Composable
fun RateInputContent(
    paddingValues: PaddingValues,
    currencyCode: String,
    input: String,
    onInputChange: (String) -> Unit,
) {
    val baseUnit = currencyUnitMap[currencyCode]?.baseUnit ?: 1
    val krwFormatted = NumberFormat.getNumberInstance(Locale.KOREA)
        .format(input.toLongOrNull() ?: 0L)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(20.dp)
    ) {
        Text(
            "예약하고 싶은 환율을\n확인해주세요",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$baseUnit $currencyCode = $krwFormatted KRW",
                style = Typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
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
    }
}
