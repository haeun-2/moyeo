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
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.res.painterResource
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
import com.d108.moyeo.R

private val TAG = "HomeScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // lifecycle 관리
    val lifecycleOwner = LocalLifecycleOwner.current

    // ViewModel의 상태를 구독합니다.
    val uiState by viewModel.uiState.collectAsState()

    // 최상단에서 아래로 당겨 새로고침
    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing = uiState.isRefreshing

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onResumed()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

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
                    navController.navigate(
                        AppScreen.Transfer.createRouteForDeposit(
                            event.boxId,
                            event.currencyId
                        )
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            HomeHeader(
                title = uiState.userName,
                onBellClick = { navController.navigate(AppScreen.Notification.route) }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(horizontal = Spacing.Medium)
            ) {
                AddBar(
                    label = "추가",
                    onClick = { navController.navigate(AppScreen.CreateBox.route) },
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.Medium)
            ) {
                WalletSummaryCard(
                    data = uiState.wallet,
                    onTitleClick = { viewModel.onWalletTitleClick() },
                    onTransferClick = { viewModel.onTransferClicked("KRW") },
                    onMoreClick = { viewModel.onWalletMoreClick() },
                    onRowClick = { currency -> viewModel.onWalletCurrencyClick(currency) }
                )

                Spacer(Modifier.height(Spacing.Large))

                PullToRefreshBox(
                    state = pullToRefreshState,
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.refresh() },
                    modifier = Modifier.weight(1f),
                ) {
                    LazyColumn(Modifier.fillMaxSize()) {
                        itemsIndexed(uiState.groups) { index, item ->
                            GroupBoxCard(
                                data = item,
                                onDepositClick = { viewModel.onDepositClick(item.id) },
                                onMoreClick = { viewModel.onGroupMoreClick(item.id) },
                                onColumnClick = { viewModel.onGroupBoxClick(item.id) },
                                onBookmarkToggle = {
                                    viewModel.onToggleBookmark(item.id, item.isBookmarked)
                                }
                            )
                            if (index < uiState.groups.lastIndex) {
                                Spacer(Modifier.height(Spacing.Large))
                            }
                        }
                    }
                }
            }

            // 바텀시트는 그대로 유지
            if (uiState.showWalletEditSheet) {
                BoxEditBottomSheet(
                    initialName = uiState.wallet.title,
                    initialColor = uiState.wallet.bg,
                    availableColors = boxAvailableColors,
                    onConfirm = viewModel::onWalletEditConfirm,
                    onDismiss = viewModel::onWalletEditDismiss
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
}

/* ---------- Header ---------- */

@Composable
fun HomeHeader(
    title: String,
    onBellClick: () -> Unit
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Medium, vertical = Spacing.Medium)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Spacing.SmallMedium),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = title,
                style = Typography.titleLarge,
            )
            IconButton(
                onClick = onBellClick,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "알림"
                )
            }
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
                    .clickable { onTitleClick() }
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
                        .padding(end = Spacing.Medium), // '>'와 '이체' 버튼 사이의 간격
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = data.title,
                        style = Typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = textColorUtil(data.bg),
                        modifier = Modifier.weight(1f)
                    )
                }
                AssistChip(
                    onClick = onTransferClick,
                    label = { Text("이체") },
                    shape = CircleShape,
                    border = null,
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = button,
                        labelColor = Color.White,
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

            HorizontalDivider(
                thickness = 0.5.dp,
                color = textColorUtil(data.bg).copy(alpha = 0.5f)
            )

            LazyColumn(
                modifier = Modifier.height(168.dp) // 스크롤 영역의 최대 높이 지정
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
        Box(Modifier
            .fillMaxWidth()
            .clickable { onColumnClick() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.Medium)
            ) {

                Text(  // 모여 박스 이름
                    text = data.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColorUtil(data.bg),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 132.dp)
                )
                Spacer(Modifier.height(Spacing.Small))


                Text(  // 금액
                    text = data.amount,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
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
                        painter = painterResource(
                            id = if (data.isBookmarked) R.drawable.round_star_24
                                 else R.drawable.round_star_border_24
                        ),
                        contentDescription = "즐겨찾기 토글",
                        tint = if (data.isBookmarked) Color.Yellow else textColorUtil(data.bg)
                    )
                }

                // TODO: 입금 색상 변경
                AssistChip(
                    onClick = onDepositClick,
                    label = { Text("입금") },
                    shape = CircleShape,
                    border = null,
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = Color.White,
                        containerColor = button,
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
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = grey,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(vertical = Spacing.Small)
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
