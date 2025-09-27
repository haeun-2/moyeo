package com.d108.moyeo.presentation.ui.screen.currency.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun FinishedContent(
    paddingValues: PaddingValues,
    currencyCode: String,
    currencyName: String,
    targetRate: String,
    amount: String,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))
        Text("환전 예약 완료!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text(
            "설정하신 환율에 도달하면 자동으로 환전 후 알려드립니다.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        InfoRow("통화", "$currencyName ($currencyCode)")
        InfoRow("지정 환율", "KRW $targetRate 기준")
        InfoRow("예약 금액", "$currencyCode $amount")
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) { Text("확인") }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
    Spacer(Modifier.height(8.dp))
}
