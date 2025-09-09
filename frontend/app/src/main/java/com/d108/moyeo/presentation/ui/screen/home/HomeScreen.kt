package com.d108.moyeo.presentation.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.*
import com.d108.moyeo.presentation.ui.component.home.WalletEditBottomSheet

@Composable
fun HomeScreen(navController: NavController) {

    // --- 상태 관리 ---
    var showWalletEditSheet by remember { mutableStateOf(false) }

    // 컬러칩
    // 바텀시트에서 사용할 색상 목록
    val availableColors = listOf(
        Color(0xFFF44336), Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7),
        Color(0xFF3F51B5), Color(0xFF2196F3), Color(0xFF03A9F4), Color(0xFF00BCD4),
        Color(0xFF009688), Color(0xFF4CAF50), Color(0xFF8BC34A), Color(0xFFCDDC39)
    )

    // 샘플 데이터 (이미지와 동일한 분위기/텍스트)
    var wallet by remember {
        mutableStateOf(
            WalletSummary(
                title = "일론머스크 딱 대",
                color = Color.Blue, // 임시 대표 색상
                balances = listOf(
                    CurrencyBalance("한국 원", "120,000 KRW"),
                    CurrencyBalance("미국 달러", "20 USD"),
                    CurrencyBalance("일본 엔", "400 JPY"),
                    CurrencyBalance("영국 파운드", "30 GBP"),
                    CurrencyBalance("유럽 유로", "15 EUR"), // 스크롤 테스트를 위해 추가
                    CurrencyBalance("중국 위안", "100 CNY")  // 스크롤 테스트를 위해 추가
                )
            )
        )
    }

    val groups = listOf(
        GroupBox("상훈 풍헌 동찬 일본 여행", "50,000 JPY", pink), // 연한 핑크
        GroupBox("미국 도대체 언제 감", "1,500 USD", brown),    // 브라운
        GroupBox("오아시스", "1,000 GBP", purple),               // 라일락
        GroupBox("유럽 갈끄니까", "2,000 EUR", Color.Cyan),     // 스크롤 테스트를 위해 추가
        GroupBox("중국 출장비", "5,000 CNY", Color.Yellow)   // 스크롤 테스트를 위해 추가
    )

    // ▼▼▼ 화면 전체를 Box로 감싸서 바텀시트를 LazyColumn과 분리합니다 ▼▼▼
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // --- 고정된 상단 영역 ---
            Spacer(Modifier.height(12.dp))
            // 헤더
            HomeHeader(
                title = "동찬",
                onBellClick = { /* TODO: 알림 화면 이동 */ }
            )
            Spacer(Modifier.height(16.dp))
            // 지갑 요약 카드
            WalletSummaryCard(
                data = wallet,
                onTransferClick = { /* TODO: 이체 */ },
                onMoreClick = { showWalletEditSheet = true },
                onRowClick = { /* TODO: 통화별 상세 이동 */ }
            )
            Spacer(Modifier.height(24.dp))

            // --- 스크롤되는 메인 콘텐츠 영역 ---
            // 이 LazyColumn이 남은 공간을 모두 차지합니다.
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // 그룹별 포켓 카드들
                items(groups) { item ->
                    GroupBoxCard(
                        data = item,
                        onDepositClick = { /* TODO: 입금 */ },
                        onMoreClick = { /* TODO: 메뉴 */ }
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }

            // --- 고정된 하단 영역 ---
            // 하단 + 버튼형 영역
            AddBar(
                label = "추가",
                onClick = { navController.navigate(AppScreen.Exchange.route) }
            )
            Spacer(Modifier.height(8.dp))
        }

        // 바텀 시트 영역 (Box의 자식이므로 LazyColumn 위에 오버레이됩니다)
        if (showWalletEditSheet) {
            WalletEditBottomSheet(
                initialName = wallet.title,
                initialColor = wallet.color,
                availableColors = availableColors,
                onConfirm = { newName, newColor ->
                    // 확인 버튼을 누르면 실제 wallet 상태를 업데이트
                    wallet = wallet.copy(title = newName, color = newColor)
                    showWalletEditSheet = false // 시트 닫기
                },
                onDismiss = {
                    showWalletEditSheet = false // 시트 닫기
                }
            )
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
    val color: Color,
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

            LazyColumn(
                modifier = Modifier.height(180.dp) // 스크롤 영역의 최대 높이 지정
            ) {
                itemsIndexed(data.balances) { index, row ->
                    WalletRow(
                        label = row.label,
                        value = row.value,
                        onClick = { onRowClick(row) }
                    )
                    if (index < data.balances.lastIndex) {
                        HorizontalDivider(thickness = 0.5.dp, color = Color.White)
                    }
                }
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
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.width(4.dp))
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
                    .padding(16.dp)
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4A4A4A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
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
                    .padding(4.dp),
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
            Spacer(Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
