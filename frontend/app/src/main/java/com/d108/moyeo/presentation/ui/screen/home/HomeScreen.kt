package com.d108.moyeo.presentation.ui.screen.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.*
import com.d108.moyeo.presentation.ui.component.home.BoxEditBottomSheet
import com.d108.moyeo.util.textColorUtil

private val TAG = "HomeScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // lifecycle 관리
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onResumed()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // ViewModel의 상태를 구독합니다.
    val uiState by viewModel.uiState.collectAsState()

    // ViewModel의 내비게이션 이벤트를 구독하고 처리
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            Log.d(TAG, "$event")
            when (event) {
                is HomeNavigationEvent.NavigateToMyWallet -> {

                    val route = AppScreen.MyWallet.createRoute(
                        boxId = event.boxId,
                        currencyCode = event.currencyCode
                    )
                    navController.navigate(route)
                }

                is HomeNavigationEvent.NavigateToMyBox -> {  // route에 {boxid}라 된 부분을 파라미터로 교체한 후 라우트
                    val route = AppScreen.MyBox.createRoute(
                        boxId = event.boxId
                    )
                    navController.navigate(route)
                }

                is HomeNavigationEvent.NavigateToTransfer -> {
                    navController.navigate(AppScreen.Transfer.createRouteForTransfer(event.currencyId))
                }

                is HomeNavigationEvent.NavigateToDeposit -> {
                    navController.navigate(AppScreen.Transfer.createRouteForDeposit(event.boxId, event.currencyId))
                }
            }
        }
    }

    // 최상단에서 아래로 당겨 새로고침
    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing = uiState.isRefreshing

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.Medium)
        ) {
            // --- 고정된 상단 영역 ---
            Spacer(Modifier.height(Spacing.SmallMedium))
            // 헤더
            HomeHeader(
                title = uiState.userName,
                onBellClick = {
                    navController.navigate(AppScreen.Notification.route)
                }
            )
            Spacer(Modifier.height(Spacing.Medium))
            // 지갑 요약 카드
            WalletSummaryCard(
                data = uiState.wallet,
                onTitleClick = { viewModel.onWalletTitleClick()},
                onTransferClick = { viewModel.onTransferClicked("KRW")},  // 한화 디폴트
                onMoreClick = { viewModel.onWalletMoreClick() },
                onRowClick = { currency ->  // 어떤 화폐가 눌렸는지 알 수 있도록 해야함
                    viewModel.onWalletCurrencyClick(currency)
                }
            )
            Spacer(Modifier.height(Spacing.Large))

            // 최상단에서 아래로 당겨 새로고침
            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refresh() }, // 새로고침 동작
                modifier = Modifier.weight(1f),
            ) {
                // --- 스크롤되는 메인 콘텐츠 영역 ---
                // 이 LazyColumn이 남은 공간을 모두 차지합니다.
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 그룹별 포켓 카드들
                    itemsIndexed(uiState.groups) { index, item ->
                        GroupBoxCard(
                            data = item,
                            onDepositClick = { viewModel.onDepositClick(item.id) },
                            onMoreClick = { viewModel.onGroupMoreClick(item.id) },
                            onColumnClick = { viewModel.onGroupBoxClick(item.id) },
                            onBookmarkToggle = { viewModel.onToggleBookmark(item.id, item.isBookmarked) }
                        )
                        if (index < uiState.groups.lastIndex) {
                            Spacer(Modifier.height(Spacing.Large))
                        }
                    }
                }
            }

            // --- 고정된 하단 영역 ---
            // 하단 + 버튼형 영역
            Spacer(Modifier.height(Spacing.Large))
            AddBar(
                label = "추가",
                onClick = { navController.navigate(AppScreen.CreateBox.route) }
            )
            Spacer(Modifier.height(Spacing.Small))
        }

        // 바텀 시트 영역 (Box의 자식이므로 LazyColumn 위에 오버레이)
        if (uiState.showWalletEditSheet) {
            BoxEditBottomSheet (
                initialName = uiState.wallet.title,
                initialColor = uiState.wallet.bg,
                availableColors = boxAvailableColors,
                onConfirm = viewModel::onWalletEditConfirm, // ViewModel 함수 호출
                onDismiss = viewModel::onWalletEditDismiss // ViewModel 함수 호출
            )
        }

        if (uiState.showGroupEditSheet) {
            val target = uiState.groups.firstOrNull { it.id == uiState.editingGroupId }
            if (target != null) {
                BoxEditBottomSheet(
                    initialName = target.title,
                    initialColor = target.bg,
                    availableColors = boxAvailableColors,
                    onConfirm = { name, color -> viewModel.onGroupEditConfirm(name, color) },
                    onDismiss = viewModel::onGroupEditDismiss
                )
            }
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
                .padding(start = Spacing.Small)
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
        colors = CardDefaults.cardColors(containerColor = data.bg)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Spacing.Large,
                        top = Spacing.SmallMedium,
                        end = Spacing.Small,
                        bottom = Spacing.Small
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTitleClick() }
                        .padding(end = Spacing.Medium), // '>'와 '이체' 버튼 사이의 간격
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = data.title,
                        style = Typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = textColorUtil(data.bg)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "back",
                        tint = textColorUtil(data.bg)
                    )
                }
                AssistChip(
                    onClick = onTransferClick,
                    label = { Text("이체") },
                    shape = CircleShape
                )
                IconButton(onClick = onMoreClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "더보기",
                        tint = textColorUtil(data.bg)
                    )
                }
            }

            Spacer(Modifier.height(Spacing.ExtraSmall))

            LazyColumn(
                modifier = Modifier.height(180.dp) // 스크롤 영역의 최대 높이 지정
            ) {
                itemsIndexed(data.balances) { index, row ->
                    WalletRow(
                        label = row.label,
                        value = row.value,
                        onClick = { onRowClick(row) },
                        bg = data.bg
                    )
                    if (index < data.balances.lastIndex) {
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = textColorUtil(data.bg).copy(alpha = 0.5f)
                        )
                    }
                }
            }
            Spacer(Modifier.height(Spacing.Small))
        }
    }
}

@Composable
private fun WalletRow(
    label: String,
    value: String,
    onClick: () -> Unit,
    bg: Color
) {
    val textColor = textColorUtil(bg)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // Row 전체를 클릭 가능하게 만듭니다.
            .padding(Spacing.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
        Spacer(Modifier.width(Spacing.ExtraSmall))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "back",
            tint = textColor
        )
    }
}

/* ---------- Group box cards (모여 박스 카드) ---------- */

@Composable
private fun GroupBoxCard(
    data: GroupBox,
    onDepositClick: () -> Unit, // 입금 클릭
    onMoreClick: () -> Unit,
    onColumnClick: () -> Unit,  // 클릭 시 상세 화면으로 이동
    onBookmarkToggle: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = data.bg),
    ) {
        Box(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.Medium)
                    .clickable { onColumnClick() }
            ) {// 이 컬럼 영역을 클릭했을 때 상세 화면으로 이동
                Text(  // 모여 박스 이름
                    text = data.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColorUtil(data.bg),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(Spacing.Small))


                Text(  // 금액
                    text = data.amount,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = textColorUtil(data.bg)
                )
            }

            // 우상단 … 버튼 + "입금" 보조 버튼
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Spacing.ExtraSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBookmarkToggle) {
                    Icon(
                        imageVector = if (data.isBookmarked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "즐겨찾기 토글",
                        tint = if (data.isBookmarked) Color.Yellow else textColorUtil(data.bg)
                    )
                }

                // TODO: 입금 색상 변경
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
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "더보기",
                        tint = textColorUtil(data.bg)
                    )
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
            Spacer(Modifier.width(Spacing.ExtraSmall))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
