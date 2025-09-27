package com.d108.moyeo.presentation.ui.screen.currency.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.ui.component.CustomKeypad
import com.d108.moyeo.presentation.ui.component.KeyMode
import com.d108.moyeo.presentation.ui.component.KeypadKey
import com.d108.moyeo.presentation.ui.component.exchange.ReservationSegTab
import com.d108.moyeo.util.currencyUnitMap

@Composable
fun AmountInputContent(
    paddingValues: PaddingValues,
    currencyCode: String,
    selectedTab: String,
    inputAmount: String,
    krwValue: Double,
    foreignValue: Double,
    onTabChange: (String) -> Unit,
    onInputChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(20.dp)
    ) {
        Row {
            Text(
                text = "$inputAmount $selectedTab".takeIf { inputAmount != "0" } ?: "0 $selectedTab",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
                    .padding(4.dp)
            ) {
                ReservationSegTab(text = currencyCode, selected = selectedTab == currencyCode) { onTabChange(currencyCode) }
                ReservationSegTab(text = "KRW", selected = selectedTab == "KRW") { onTabChange("KRW") }
            }
        }

        Spacer(Modifier.height(10.dp))
        val convertedText =
            if (selectedTab == currencyCode && krwValue != 0.0) "약 %,d원".format(krwValue.toLong())
            else if (selectedTab == "KRW" && foreignValue != 0.0) "약 %,.2f $currencyCode".format(foreignValue)
            else ""
        if (convertedText.isNotBlank()) Text(convertedText, color = Color.Gray)

        Spacer(Modifier.weight(1f))
        CustomKeypad(
            onKeyPress = { key ->
                when (key) {
                    is KeypadKey.Digit -> {
                        val next = if (inputAmount == "0") "${key.value}" else inputAmount + key.value
                        onInputChange(next)
                    }
                    KeypadKey.Clear -> { // Zeros 모드에서 "00"
                        val next = if (inputAmount == "0") "0" else inputAmount + "00"
                        onInputChange(next)
                    }
                    KeypadKey.Backspace -> {
                        val next = inputAmount.dropLast(1).ifBlank { "0" }
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
