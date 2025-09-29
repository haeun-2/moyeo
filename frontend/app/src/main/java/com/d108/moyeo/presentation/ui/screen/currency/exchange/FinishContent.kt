package com.d108.moyeo.presentation.ui.screen.currency.exchange

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import java.text.NumberFormat
import java.util.Locale

// FinishContent.kt

@Composable
fun FinishContent(
    mode: ExchangeMode,
    chargedAmount: String,
    chargedCurrency: String,
    spentAmount: Double,
    spentCurrency: String,
    onDone: () -> Unit
) {
    // 숫자를 통화 형식(,)으로 변환하기 위한 포맷터
    val krwFormat = NumberFormat.getCurrencyInstance(Locale.KOREA).apply {
        maximumFractionDigits = 0
    }
    val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 메인 타이틀
        Text(
            text = if (mode == ExchangeMode.CHARGE) "충전 완료!" else "환불 완료!",
            style = Typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(Spacing.Medium))

        // 2. 충전된 외화 금액 (가장 중요하게 표시)
        Text(
            text = "${numberFormat.format(chargedAmount.toLongOrNull() ?: 0L)} $chargedCurrency",
            style = Typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(Spacing.ExtraLarge))

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("완료", style = Typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}