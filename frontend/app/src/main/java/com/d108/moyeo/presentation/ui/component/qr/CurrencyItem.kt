package com.d108.moyeo.presentation.ui.component.qr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.d108.moyeo.domain.model.box.Balance
import com.d108.moyeo.presentation.theme.Typography
import java.text.DecimalFormat

@Composable
fun CurrencyItem(
    balance: Balance,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    // 금액에 천 단위 쉼표를 추가하기 위한 포맷터
    val formattedAmount = DecimalFormat("#,###.##").format(balance.balance)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(balance.currency, style = Typography.labelSmall, color = textColor)
        Text(formattedAmount, style = Typography.labelMedium, color = textColor)
    }
}