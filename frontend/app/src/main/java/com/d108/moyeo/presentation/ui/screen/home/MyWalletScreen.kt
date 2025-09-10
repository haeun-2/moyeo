package com.d108.moyeo.presentation.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.home.mywallet.MyWalletCurrencyBottomSheet
import com.d108.moyeo.presentation.ui.component.home.mywallet.MyWalletFilterBottomSheet


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWalletScreen(
    navController: NavController,
    viewModel: MyWalletViewModel = viewModel()
) {
    // ViewModel의 상태를 구독합니다.
    val uiState by viewModel.uiState.collectAsState()


    // 잔액 클릭 시 열릴 바텀 시트
    if (uiState.showCurrencySheet) {
        MyWalletCurrencyBottomSheet(
            currencies = uiState.currencies,
            onItemSelected = viewModel::onCurrencySelected,
            onDismiss = viewModel::onCurrencySheetDismiss
        )
    }

    // 필터 클릭 시 열릴 바텀 시트
    if (uiState.showFilterSheet) {
        MyWalletFilterBottomSheet(
            initialFilters = uiState.filters,
            onConfirm = viewModel::onFilterConfirm,
            onDismiss = viewModel::onFilterSheetDismiss
        )
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO: 충전하기 로직 */ },
                icon = { Icon(Icons.Default.Add, "충전하기 아이콘") },
                text = { Text(text = "충전하기") }
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // FAB에 가려지지 않도록 패딩 적용
                .padding(horizontal = Spacing.Medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 정보 카드
            TopWalletInfoCard(
                walletName = uiState.walletName,
                totalBalance = uiState.totalBalance,
                onBalanceClick = viewModel::onBalanceClick
            )

            // 검색 및 필터 바
            SearchAndFilterBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChanged, // 이벤트 연결
                filters = uiState.filters,
                onFilterClick = viewModel::onFilterClick
            )

            // 거래 내역 목록
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uiState.transactions) { transaction ->
                    TransactionRowItem(transaction = transaction)
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                }
            }
        }
    }
}

// 상단 정보 카드 UI
@Composable
private fun TopWalletInfoCard(
    walletName: String,
    totalBalance: String,
    onBalanceClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp), // 높이를 200dp로 조정
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.Medium),
            horizontalAlignment = Alignment.CenterHorizontally,  // 이 속성으로 수평 중앙 정렬
            verticalArrangement = Arrangement.SpaceBetween  // 이 속성으로 영역 구분
        ) {
            Text(
                text = walletName,  // 여기에는 내 월렛의 이름이 떠야 함.
                style = Typography.titleLarge
            )

            Row(  // 잔액이 보이는 영역
                modifier = Modifier.clickable { onBalanceClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = totalBalance, // 임시 잔액
                    style = Typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "화폐 선택"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // 버튼 사이에 간격.
                // !!그리고 이 버튼들이 너무 크다. 좀 작아진 다음에 좌우와 간격이 있으면 좋겠는데. !!
            ) {
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f), // 버튼이 남은 공간을 균등하게 차지하도록
                ) {
                    Text("보내기")
                }

                Button(
                    onClick = { /*TODO*/ },
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
    filters: FilterOptions,
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
            onValueChange = { onSearchQueryChange },
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
private fun TransactionRowItem(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.Medium),
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