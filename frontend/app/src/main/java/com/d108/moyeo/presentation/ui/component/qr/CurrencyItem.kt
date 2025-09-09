package com.d108.moyeo.presentation.ui.component.qr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun CurrencyItem(
    currencyName: String,
    amount: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(currencyName, style = Typography.labelSmall)
        Text(amount, style = Typography.labelMedium)
    }
}