package com.d108.moyeo.presentation.ui.screen.currency.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeContent(
    paddingValues: PaddingValues,
    isLoading: Boolean,
    reservations: List<ReservationListItem> = emptyList(),
    onRefresh: () -> Unit,
    onStartReservation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(20.dp)
    ) {
        Text(
            text = "내 지갑에 보유 중인 원화가 출금되어 환전됩니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "유효 기간 최대 6개월",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onStartReservation,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(24.dp)
        ) { Text("환전 예약하기", color = Color.White, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium) }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("등록된 예약 (${reservations.size}개)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            TextButton(onClick = onRefresh, enabled = !isLoading) {
                Text(if (isLoading) "새로고침중..." else "새로고침", style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(Modifier.height(12.dp))

        when {
            isLoading && reservations.isEmpty() -> {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            reservations.isEmpty() -> {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("등록된 예약이 없습니다", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(reservations, key = { it.id }) { item ->
                        ReservationListCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReservationListCard(item: ReservationListItem) {
    ElevatedCard {
        Column(Modifier.padding(16.dp)) {
            Text("${item.currencyName} (${item.currencyCode})", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            InfoRow("지정 환율", "KRW ${item.targetRate} 기준")
            InfoRow("원화 금액", "KRW ${item.amountKrw}")
            InfoRow("만료 일자", item.expiresAt)
        }
    }
}

@Composable private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
    Spacer(Modifier.height(4.dp))
}

/** 리스트 전용 임시 모델 (실연동 시 제거/교체) */
data class ReservationListItem(
    val id: String,
    val currencyCode: String,
    val currencyName: String,
    val targetRate: String,
    val amountKrw: String,
    val expiresAt: String
)
