package com.d108.moyeo.presentation.ui.screen.home.box

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.button
import com.d108.moyeo.presentation.theme.onPrimaryLight
import com.d108.moyeo.presentation.ui.component.home.CommonFilterBottomSheet
import com.d108.moyeo.presentation.ui.component.home.CurrencyBottomSheet
import com.d108.moyeo.presentation.ui.component.home.FilterOptions


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBoxScreen(
    navController: NavController,
    boxId: String,
    bgColor: Int?,  // 배경색을 위한 파라미터
    viewModel: MyBoxViewModel = viewModel()
) {
    // ViewModel의 상태를 구독
    val uiState by viewModel.uiState.collectAsState()

    // 화면이 처음 생성되거나 boxId가 변경될 때, ViewModel에 데이터 로드를 요청.
    LaunchedEffect(key1 = boxId) {
        viewModel.loadBoxDetails(boxId)
    }

    LaunchedEffect(key1 = true) {  // 내비게이션 이벤트 구독 및 처리
        viewModel.navigationEvent.collect { event ->
            when(event) {
                is MyBoxNavigationEvent.NavigateToCollecting -> {
                    navController.navigate(AppScreen.Collecting.createRoute(event.boxId, event.currencyCode)) // 원하는 currency 전달
                }
                is MyBoxNavigationEvent.NavigateToCalculating -> {
                    navController.navigate(AppScreen.Calculating.createRoute(event.boxId, event.currencyCode)) // 원하는 currency 전달
                }
            }
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

    Scaffold { innerPadding ->
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
                boxName = uiState.boxName,
                totalAmount = uiState.totalAmount,
                backgroundColor = Color(bgColor!!),
                onAmountClick = viewModel::onAmountClick,
                onBackClick = { /* TODO: 뒤로가기 로직 추가 */ },
                onCollectingClick = viewModel::onCollectingClick,
                onCalculatingClick = viewModel::onCalculatingClick,
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
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.transactions) { transaction ->
                        BoxTransactionRowItem(
                            transaction = transaction,
                            onClick = {
                                // AppScreen에 정의된 경로를 사용하여, 클릭된 transaction의 id를 전달합니다.
                                navController.navigate(
                                    AppScreen.MyBoxDetail.route.replace("{transactionId}", transaction.id)
                                )
                            }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
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
    totalAmount: String,
    backgroundColor: Color,
    onAmountClick: () -> Unit,
    onBackClick: () -> Unit,
    onCollectingClick: () -> Unit,
    onCalculatingClick:() -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        tonalElevation = 0.dp,
        color = backgroundColor,
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

                Text(
                    text = boxName,
                    style = Typography.titleLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
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
                            onClick = onAmountClick,
                        )
                        .padding(Spacing.Small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = totalAmount,
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
                    onClick = onCollectingClick,  // 이 모으기 버튼 클릭했을 때 할 일을 할 거야
                    colors = ButtonDefaults.buttonColors(
                        containerColor = button,
                        contentColor = onPrimaryLight
                    ),
                    modifier = Modifier.weight(1f),
                ) {
                    Text("모으기") // 버튼 텍스트 수정
                }

                Button(
                    onClick = onCalculatingClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = button,
                        contentColor = onPrimaryLight
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("정산하기") // 버튼 텍스트 수정

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
            Text(filters.period, style = Typography.bodySmall)
            Text(" | ", style = Typography.bodySmall, color = Color.Gray)
            Text(filters.scope, style = Typography.bodySmall)
            Text(" | ", style = Typography.bodySmall, color = Color.Gray)
            Text(filters.sort, style = Typography.bodySmall)
        }
    }
}

// 거래 내역 한 줄 UI
@Composable
private fun BoxTransactionRowItem(
    transaction: BoxTransaction,
    onClick: () -> Unit
) {
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
            Text(transaction.date, style = Typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.width(Spacing.Medium))
            Text(
                text = transaction.description,
                style = Typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(transaction.amount, style = Typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(transaction.balance, style = Typography.bodySmall, color = Color.Gray)
            }
        }

    }
}
