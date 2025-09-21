package com.d108.moyeo.presentation.ui.screen.home.wallet

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d108.moyeo.domain.model.history.HistoryTransaction
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.button
import com.d108.moyeo.presentation.theme.errorLight
import com.d108.moyeo.presentation.theme.onPrimaryLight
import com.d108.moyeo.presentation.ui.component.history.toDate
import com.d108.moyeo.presentation.ui.component.home.CommonFilterBottomSheet
import com.d108.moyeo.presentation.ui.component.home.CurrencyBottomSheet
import com.d108.moyeo.presentation.ui.component.home.FilterOptions
import com.google.gson.Gson
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

private val TAG = "MyWalletScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWalletScreen(
    navController: NavController,
    viewModel: MyWalletViewModel = hiltViewModel()
) {
    // ViewModel의 상태를 구독
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is WalletNavigationEvent.NavigateToTransfer -> {
                    // "보내기" 이벤트가 오면, currencyCode를 가지고 TransferScreen으로 이동
                    navController.navigate(
                        AppScreen.Transfer.createRouteForTransfer(event.currencyCode)
                    )
                }
                is WalletNavigationEvent.NavigateToCharge -> {
                    navController.navigate(AppScreen.Charge.route)
                }
            }
        }
    }

    // 무한 스크롤 로직
    val isScrolledToEnd by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 5
        }
    }

    LaunchedEffect(isScrolledToEnd) {
        if (isScrolledToEnd) {
            viewModel.loadNextPage()
        }
    }


    // 잔액 클릭 시 열릴 바텀 시트
    if (uiState.showCurrencySheet) {
        CurrencyBottomSheet(
            currencies = uiState.currencies,
            onItemSelected = viewModel::onCurrencySelected,
            onDismiss = viewModel::onCurrencySheetDismiss
        )
    }

    // 필터 클릭 시 열릴 바텀 시트
    if (uiState.showFilterSheet) {
        CommonFilterBottomSheet(
            initialFilters = uiState.filters.toAdapter(),  // Wallet -> Adapter
            onConfirm = { updatedFilters ->
                // 바텀시트가 전달해준 '어댑터'를 '내부 모델'로 변환하여 ViewModel에 전달
                viewModel.onFilterConfirm(updatedFilters.toWallet())
            },
            onDismiss = viewModel::onFilterSheetDismiss
        )
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = viewModel::onChargeClick,  // 여기에서 충전하기 화면으로 이동
                icon = { Icon(Icons.Default.Add, "충전 아이콘") },
                text = { Text(text = "충전") }
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        val layoutDir = LocalLayoutDirection.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateLeftPadding(layoutDir),
                    end = innerPadding.calculateRightPadding(layoutDir),
                    bottom = innerPadding.calculateBottomPadding()
                ), // FAB에 가려지지 않도록 패딩 적용
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 정보 카드
            TopWalletInfoSurface(
                walletName = uiState.walletInfo?.title ?: "내 지갑",
                totalBalance = uiState.walletInfo?.balances
                    ?.find { it.currency == uiState.selectedCurrencyCode }
                    ?.let { "${DecimalFormat("#,###.##").format(it.balance)} ${it.currency}" }
                    ?: "전체 보기",
                onBalanceClick = viewModel::onBalanceClick,
                onBackClick = { navController.popBackStack() },
                onTransferClick = viewModel::onTransferClick
            )

            Column(modifier = Modifier.padding(horizontal = Spacing.Medium)) {
                SearchAndFilterBar(  // 검색 및 필터바
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = viewModel::onSearchQueryChanged, // 이벤트 연결
                    filters = uiState.filters,
                    onFilterClick = viewModel::onFilterClick
                )

                // 거래 내역 목록
                if (uiState.isLoading && uiState.transactions.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.errorMessage != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.errorMessage!!)
                    }
                } else {
                    // 거래 내역 목록
                    LazyColumn(
                        state = listState, // 무한 스크롤
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.transactions) { transaction ->
                            TransactionRowItem(
                                transaction = transaction,
                                onClick = {
                                    val transactionJson = Gson().toJson(transaction)
                                    navController.navigate(
                                        AppScreen.MyWalletDetail.createRoute(
                                            historyId = transaction.id,
                                            transactionJson = transactionJson
                                        )
                                    )
                                }
                            )
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        }
                        // 6. 다음 페이지 로딩 중일 때 하단에 인디케이터 표시
                        if (uiState.isLoadingNextPage) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Spacing.Medium),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 상단 정보 카드 UI
@Composable
private fun TopWalletInfoSurface(
    walletName: String,
    totalBalance: String,
    onBalanceClick: () -> Unit,
    onBackClick: () -> Unit,
    onTransferClick:() -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp), // 높이를 200dp로 조정
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.Medium),
//            horizontalAlignment = Alignment.CenterHorizontally,  // 이 속성으로 수평 중앙 정렬
            verticalArrangement = Arrangement.SpaceBetween  // 이 속성으로 영역 구분
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "뒤로가기"
                    )
                }

                Text(
                    text = walletName,
                    style = Typography.titleLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Row(  // 잔액이 보이는 영역
                modifier = Modifier
                    .fillMaxWidth(),
//                    .clickable { onBalanceClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Row (
                    modifier = Modifier
                        .clickable(
                            role = Role.Button,
                            onClick = onBalanceClick,
                        )
                        .padding(Spacing.Small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = totalBalance, // 임시 잔액
                        style = Typography.displayLarge,
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "화폐 선택"
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(32.dp) // 버튼 사이에 간격.
                // !!그리고 이 버튼들이 너무 크다. 좀 작아진 다음에 좌우와 간격이 있으면 좋겠는데. !!
            ) {
                Button(
                        onClick = onTransferClick, // !! 이 버튼이랑 연결되어야 함
                    colors = ButtonDefaults.buttonColors(
                        containerColor = button,
                        contentColor = onPrimaryLight
                    ),
                    modifier = Modifier.weight(1f), // 버튼이 남은 공간을 균등하게 차지하도록
                ) {
                    Text("보내기")
                }

                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = button,
                        contentColor = onPrimaryLight
                    ),
                    modifier = Modifier.weight(1f) // 버튼이 남은 공간을 균등하게 차지하도록
                ) {
                    Text("환전하기")
                }
            }
        }
    }
}

// 검색 및 필터 바 UI
@Composable
private fun SearchAndFilterBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filters: WalletFilterOptions,
    onFilterClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.Small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 좌측 끝에는 돋보기 버튼.
        Icon(Icons.Default.Search, contentDescription = "검색 아이콘")
        // 남은 공간에는 검색용 인풋텍스트
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Spacing.Small),
            placeholder = { Text("검색", style = Typography.bodySmall) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { /* TODO: 검색 로직 */ }),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        // 우측에는 1개월, 전체, 최신순 등 텍스트 버튼
        Row(
            modifier = Modifier.clickable { onFilterClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(filters.period.displayName, style = Typography.bodySmall)
            Text(" | ", style = Typography.bodySmall, color = Color.Gray)
            Text(filters.scope, style = Typography.bodySmall)
            Text(" | ", style = Typography.bodySmall, color = Color.Gray)
            Text(filters.sort.displayName, style = Typography.bodySmall)
        }
    }
}

// 거래 내역 한 줄 UI
@Composable
private fun TransactionRowItem(
    transaction: HistoryTransaction,
    onClick: () -> Unit
) {

//    Log.d(TAG, "TransactionRowItem: ${transaction.datetime}")
    // TransactionRowItem: 2025-09-20 20:22:00
    val formattedAmount = DecimalFormat("#,###.##").format(transaction.amount)
    val date = transaction.datetime.substring(startIndex = 0, endIndex = 10)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        color = Color.Transparent,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.Large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(date, style = Typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.width(Spacing.Medium))
            Text(
                text = transaction.title,
                style = Typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$formattedAmount ${transaction.currency}",
                    style = Typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (transaction.amount < 0) errorLight else Color.Yellow
                )
                Text(
                    text = "${DecimalFormat("#,###.##").format(transaction.balance)} ${transaction.currency}",
                    style = Typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}