package com.d108.moyeo.presentation.ui.screen.home

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.*
import com.d108.moyeo.presentation.ui.component.home.WalletEditBottomSheet

@Composable
fun HomeScreen(navController: NavController,
               viewModel: HomeViewModel = viewModel()) {

    // ViewModel의 상태를 구독합니다.
    val uiState by viewModel.uiState.collectAsState()

    // ViewModel의 내비게이션 이벤트를 구독하고 처리
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is HomeNavigationEvent.NavigateToMyWallet -> {
                    navController.navigate(AppScreen.MyWallet.route)
                }
            }
        }
    }

    // 컬러칩
    // 바텀시트에서 사용할 색상 목록
    val availableColors = listOf(
        Color(0xFFF44336), Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7),
        Color(0xFF3F51B5), Color(0xFF2196F3), Color(0xFF03A9F4), Color(0xFF00BCD4),
        Color(0xFF009688), Color(0xFF4CAF50), Color(0xFF8BC34A), Color(0xFFCDDC39)
    )

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
                onBellClick = {
                    navController.navigate(AppScreen.Notification.route)
                }
            )
            Spacer(Modifier.height(16.dp))
            // 지갑 요약 카드
            WalletSummaryCard(
                data = uiState.wallet,
                onTitleClick = { viewModel.onWalletTitleClick()},
                onTransferClick = { /* TODO: 이체 */ },
                onMoreClick = { viewModel.onWalletMoreClick() },
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
                items(uiState.groups) { item ->
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

        // 바텀 시트 영역 (Box의 자식이므로 LazyColumn 위에 오버레이)
        if (uiState.showWalletEditSheet) {
            WalletEditBottomSheet(
                initialName = uiState.wallet.title,
                initialColor = uiState.wallet.color,
                availableColors = availableColors,
                onConfirm = viewModel::onWalletEditConfirm, // ViewModel 함수 호출
                onDismiss = viewModel::onWalletEditDismiss // ViewModel 함수 호출
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

@Composable
private fun WalletSummaryCard(
    data: WalletSummary,
    onTitleClick: () -> Unit,
    onTransferClick: () -> Unit,  // 이체 버튼 클릭
    onMoreClick: () -> Unit,  // 삼점 클릭
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
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTitleClick() }
                        .padding(end = 16.dp), // '>'와 '이체' 버튼 사이의 간격
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = data.title,
                        style = Typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = ">",
                        modifier = Modifier.weight(1f), // > 글자가 남은 공간을 모두 차지하도록
                        style = Typography.titleMedium,
                    )
                }
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

/* ---------- Group box cards (모여 박스 카드) ---------- */

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
