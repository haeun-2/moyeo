package com.d108.moyeo.presentation.ui.screen.currency.exchange

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun FinishContent(
    mode: ExchangeMode,
    amount: String,
    currencyUnit: String,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (mode == ExchangeMode.CHARGE) "충전 완료!" else "반환 완료!",
            style = Typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(Spacing.Medium))
        Text("$amount $currencyUnit", style = Typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}
