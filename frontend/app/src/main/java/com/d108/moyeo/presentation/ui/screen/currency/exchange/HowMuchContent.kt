package com.d108.moyeo.presentation.ui.screen.currency.exchange

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.CustomKeypad
import com.d108.moyeo.presentation.ui.component.KeyMode
import com.d108.moyeo.presentation.ui.component.KeypadKey

@Composable
fun HowMuchContent(
    title: String,
    currencyName: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column {
        Text(text = title, style = Typography.bodyMedium)
        Spacer(Modifier.height(Spacing.Medium))
        Text(text = currencyName, style = Typography.bodyLarge, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(Spacing.Small))
        Text(text = amount, style = Typography.displayLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(Spacing.Medium))

        CustomKeypad(
            onKeyPress = { key ->
                when (key) {
                    is KeypadKey.Digit -> {
                        val next = if (amount == "0") "${key.value}" else amount + key.value
                        onAmountChange(next)
                    }
                    KeypadKey.Clear -> {
                        val next = if (amount == "0") "0" else amount + "00"
                        onAmountChange(next)
                    }
                    KeypadKey.Backspace -> {
                        val next = amount.dropLast(1).ifEmpty { "0" }
                        onAmountChange(next)
                    }
                    is KeypadKey.Custom -> {}
                }
            },
            keypadColortype = "normal",
            keyMode = KeyMode.Zeros,
            buttonAspectRatio = 1.2f,
            modifier = Modifier
                .padding(horizontal = Spacing.Large)
                .height(320.dp)
        )
    }
}
