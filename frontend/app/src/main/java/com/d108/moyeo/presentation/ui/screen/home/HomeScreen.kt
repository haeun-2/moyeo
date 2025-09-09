package com.d108.moyeo.presentation.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.*

@Composable
fun HomeScreen(navController: NavController) {
    // 샘플 데이터 (이미지와 동일한 분위기/텍스트)
    val wallet = WalletSummary(
        title = "일론머스크 딱 대",
        balances = listOf(
            CurrencyBalance("한국 원", "120,000 KRW"),
            CurrencyBalance("미국 달러", "20 USD"),
            CurrencyBalance("일본 엔", "400 JPY"),
            CurrencyBalance("영국 파운드", "30 GBP"),
        )
    )
    val groups = listOf(
        GroupBox("상훈 풍헌 동찬 일본 여행", "50,000 JPY", pink), // 연한 핑크
        GroupBox("미국 도대체 언제 감", "1,500 USD", brown),    // 브라운
        GroupBox("오아시스", "1,000 GBP", purple)                // 라일락
    )

    // 상단 여백 포함 전체 스크롤
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item { Spacer(Modifier.height(12.dp)) }
        // 헤더
        item {
            HomeHeader(
                title = "동찬",
                onBellClick = { /* TODO: 알림 화면 이동 */ }
            )
            Spacer(Modifier.height(16.dp))
        }
        // 지갑 요약 카드
        item {
            WalletSummaryCard(
                data = wallet,
                onTransferClick = { /* TODO: 이체 */ },
                onMoreClick = { /* TODO: 메뉴 */ },
                onRowClick = { /* TODO: 통화별 상세 이동 */ }
            )
            Spacer(Modifier.height(24.dp))
        }
        // 그룹별 포켓 카드들
        items(groups) { item ->
            GroupBoxCard(
                data = item,
                onDepositClick = { /* TODO: 입금 */ },
                onMoreClick = { /* TODO: 메뉴 */ }
            )
            Spacer(Modifier.height(24.dp))
        }
        // 하단 + 버튼형 영역
        item {
            AddBar(
                label = "추가",
                onClick = { navController.navigate(AppScreen.Exchange.route) }
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

/* ---------- Header ---------- */

@Composable
private fun HomeHeader(
    title: String,
    onBellClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = Typography.headlineSmall,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
        IconButton(onClick = onBellClick) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "알림"
            )
        }
    }
}

/* ---------- Wallet summary (상단 흰 카드) ---------- */

private data class WalletSummary(
    val title: String,
    val balances: List<CurrencyBalance>
)

private data class CurrencyBalance(val label: String, val value: String)

@Composable
private fun WalletSummaryCard(
    data: WalletSummary,
    onTransferClick: () -> Unit,
    onMoreClick: () -> Unit,
    onRowClick: (CurrencyBalance) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceContainerLight)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 12.dp, end = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                AssistChip(
                    onClick = onTransferClick,
                    label = { Text("이체") },
                    shape = CircleShape
                )
                IconButton(onClick = onMoreClick) {
                    Icon(Icons.Default.MoreVert, contentDescription = "더보기")
                }
            }
            Spacer(Modifier.height(4.dp))
            data.balances.forEachIndexed { index, row ->
                WalletRow(
                    label = row.label,
                    value = row.value,
                    onClick = { onRowClick(row) }
                )
                if (index != data.balances.lastIndex)
                    HorizontalDivider(thickness = 0.5.dp, color = Color.White)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun WalletRow(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.width(6.dp))
        Text(">", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6C6C6C))
    }
}

/* ---------- Group box cards (컬러 카드) ---------- */

private data class GroupBox(
    val title: String,
    val amount: String,
    val bg: Color
)

@Composable
private fun GroupBoxCard(
    data: GroupBox,
    onDepositClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = data.bg),
    ) {
        Box(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 14.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4A4A4A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = data.amount,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.Black
                )
            }

            // 우상단 … 버튼 + "입금" 보조 버튼
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = onDepositClick,
                    label = { Text("입금") },
                    shape = CircleShape,
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = Color.Black,
                        containerColor = Color(0x30FFFFFF),
                        leadingIconContentColor = Color.Black
                    )
                )
                IconButton(onClick = onMoreClick) {
                    Icon(Icons.Default.MoreVert, contentDescription = "더보기")
                }
            }
        }
    }
}

/* ---------- Add bar (하단 + 바) ---------- */

@Composable
private fun AddBar(
    label: String,
    onClick: () -> Unit
) {
    val bg = Color(0xFFE0E0E0)
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bg,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = "추가")
            Spacer(Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
