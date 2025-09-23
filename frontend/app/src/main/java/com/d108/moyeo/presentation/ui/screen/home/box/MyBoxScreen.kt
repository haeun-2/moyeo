package com.d108.moyeo.presentation.ui.screen.home.box

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.d108.moyeo.domain.model.history.HistoryTransaction
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.button
import com.d108.moyeo.presentation.theme.errorLight
import com.d108.moyeo.presentation.theme.onPrimaryLight
import com.d108.moyeo.presentation.ui.component.home.CommonFilterBottomSheet
import com.d108.moyeo.presentation.ui.component.home.CurrencyBottomSheet
import com.d108.moyeo.presentation.ui.component.home.FilterOptions
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.DecimalFormat

private val TAG = "MyBoxScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBoxScreen(
    navController: NavController,
    viewModel: MyBoxViewModel = hiltViewModel()
) {
    // ViewModel의 상태를 구독
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // [수정] scope.launch로 코루틴을 시작합니다.
                scope.launch {
                    // 1. ViewModel의 forceRefresh가 끝날 때까지 '반드시' 기다립니다.
                    viewModel.forceRefresh()

                    // 2. forceRefresh가 완전히 끝난 후에, 검색어/카테고리 확인 로직을 실행합니다.
                    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
                    val searchQuery = savedStateHandle?.get<String>("search_query")
                    if (searchQuery != null) {
                        viewModel.searchWithQuery(searchQuery)
                        savedStateHandle.remove<String>("search_query")
                    } else {
                        val searchCategory = savedStateHandle?.get<String>("search_category")
                        if (searchCategory != null) {
                            viewModel.searchWithCategory(searchCategory)
                            savedStateHandle.remove<String>("search_category")
                        }
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(key1 = true) {  // 내비게이션 이벤트 구독 및 처리
        viewModel.navigationEvent.collect { event ->
            when(event) {
                is MyBoxNavigationEvent.NavigateToCollect -> {
                    navController.navigate(
                        AppScreen.Transfer.createRouteForDeposit(
                            boxId = viewModel.boxId,
                            currencyId = event.currencyCode
                        )
                    )
                }
                is MyBoxNavigationEvent.NavigateToCalculate -> {
                    navController.navigate(AppScreen.Calculate.createRoute(event.boxId)) // 복수의 화폐 정산 가능
                }
                is MyBoxNavigationEvent.InviteLinkReady -> {
                    clipboard.setText(AnnotatedString(event.link))
                    Toast.makeText(context, "초대 링크를 클립보드에 복사했어요.", Toast.LENGTH_SHORT).show()
                }
                is MyBoxNavigationEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is MyBoxNavigationEvent.NavigateToMember -> {
                    navController.navigate(AppScreen.Member.createRoute(event.boxId))
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

    // 잔액 클릭 시 열림
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
            initialFilters = uiState.filters.toAdapter(),             // Box -> Adapter
            onConfirm = { updated: FilterOptions ->
                viewModel.onFilterConfirm(updated.toBox())           // Adapter -> Box
            },
            onDismiss = viewModel::onFilterSheetDismiss
        )
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = viewModel::onCalculateClick,  // 여기에서 정산하기 화면으로 이동
                icon = { Icon(Icons.Default.ThumbUp, "정산 아이콘") },
                text = { Text(text = "정산하기") }
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
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 정보 카드
            TopBoxInfoSurface(
                boxName = uiState.boxInfo?.title ?: "내 박스",
                totalBalance = uiState.boxInfo?.balances
                    ?.find { it.currency == uiState.selectedCurrencyCode }
                    ?.let { "${DecimalFormat("#,###.##").format(it.balance)} ${it.currency}" }
                    ?: "전체 보기",
                onBalanceClick = viewModel::onBalanceClick,
                onInviteClick = viewModel::onInviteClick,
                onBackClick = { navController.popBackStack() },
                onCollectClick = viewModel::onCollectClick,
                onExchangeClick = viewModel::onExchangeClick,
                onMemberClick = viewModel::onMemberClick,
                bg = uiState.boxInfo?.bg ?: Color.Blue,
                textColor = uiState.boxInfo?.textColor ?: Color.Black
            )

            // 검색 및 필터 바 (MyWalletScreen의 구조 재사용)
            Column (
                modifier = Modifier.padding(horizontal = Spacing.Medium)
            ) {
                SearchAndFilterBar(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = viewModel::onSearchQueryChanged,
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
                            BoxTransactionRowItem(
                                transaction = transaction,
                                onClick = {
                                    val transactionJson = Gson().toJson(transaction)
                                    navController.navigate(
                                        AppScreen.MyBoxDetail.createRoute(
                                            boxId = uiState.boxInfo!!.id,
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
private fun TopBoxInfoSurface(
    boxName: String,
    totalBalance: String,
    onBalanceClick: () -> Unit,
    onBackClick: () -> Unit,
    onCollectClick: () -> Unit,
    onExchangeClick: () -> Unit,
    onInviteClick: () -> Unit,
    onMemberClick: () -> Unit,
    bg: Color,
    textColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        tonalElevation = 0.dp,
        color = bg,
        contentColor = textColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.Medium),
//            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
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
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = boxName, style = Typography.titleLarge)

                    Spacer(Modifier.height(6.dp))
                    AssistChip(              // ← 작게 들어가는 버튼
                        onClick = onMemberClick,
                        label = { Text("회원 목록") }
                    )
                }

                IconButton(
                    onClick = onInviteClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) { Icon(Icons.Default.Share, contentDescription = "초대하기") }

                Text(
                    text = boxName,
                    style = Typography.titleLarge,
                    modifier = Modifier.align(Alignment.Center)
                )

                IconButton(
                    onClick = onInviteClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "초대하기"
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
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
                        text = totalBalance,
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
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Button(
                    onClick = onCollectClick,  // 이 모으기 버튼 클릭했을 때 할 일을 할 거야
                    colors = ButtonDefaults.buttonColors(
                        containerColor = button,
                        contentColor = onPrimaryLight
                    ),
                    modifier = Modifier.weight(1f),
                ) {
                    Text("모으기") // 버튼 텍스트 수정
                }

                Button(
                    onClick = {
                        // TODO: 환전하기 로직
                        onExchangeClick
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = button,
                        contentColor = onPrimaryLight
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("환전하기") // 버튼 텍스트 수정
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
    filters: BoxFilterOptions,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.Small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, contentDescription = "검색 아이콘")
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
private fun BoxTransactionRowItem(
    transaction: HistoryTransaction,
    onClick: () -> Unit
) {
    val formattedAmount = DecimalFormat("#,###.##").format(transaction.amount)
    val date = transaction.datetime.substring(startIndex = 0, endIndex = 10)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        color =  Color.Transparent,
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
                    color = if (transaction.amount < 0) errorLight else Color.Blue
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
