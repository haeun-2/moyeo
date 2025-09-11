    package com.d108.moyeo.presentation.ui.screen.home

    import androidx.compose.foundation.background
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.text.KeyboardActions
    import androidx.compose.foundation.text.KeyboardOptions
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.KeyboardArrowDown
    import androidx.compose.material.icons.filled.MoreVert
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
    import com.d108.moyeo.presentation.ui.component.home.mybox.MyBoxCurrencyBottomSheet


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MyBoxScreen(
        navController: NavController,
        boxId: String,
        viewModel: MyBoxViewModel = viewModel()
    ) {

        // ViewModel의 상태를 구독
        val uiState by viewModel.uiState.collectAsState()

        // 화면이 처음 생성되거나 boxId가 변경될 때, ViewModel에 데이터 로드를 요청.
        LaunchedEffect(key1 = boxId) {
            viewModel.loadBoxDetails(boxId)
        }

        if (uiState.showCurrencySheet) {
            MyBoxCurrencyBottomSheet(
                currencies = uiState.currencies,
                onItemSelected = viewModel::onCurrencySelected,
                onDismiss = viewModel::onCurrencySheetDismiss
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.Medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 정보 카드
            TopBoxInfoCard(
                boxName = uiState.boxName,
                totalAmount = uiState.totalAmount,
                onAmountClick = viewModel::onAmountClick
            )

            // 검색 및 필터 바 (MyWalletScreen의 구조 재사용)
            SearchAndFilterBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChanged,
                filters = uiState.filters,
                onFilterClick = { /* TODO: 필터 바텀시트 열기 */ }
            )

            // 거래 내역 목록
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uiState.transactions) { transaction ->
                    BoxTransactionRowItem(transaction = transaction)
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                }
            }
        }
    }
    // 상단 정보 카드 UI
    @Composable
    private fun TopBoxInfoCard(
        boxName: String,
        totalAmount: String,
        onAmountClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.Medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = boxName,
                    style = Typography.titleLarge,
                )

                Row(
                    modifier = Modifier.clickable { onAmountClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = totalAmount,
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("모으기") // 버튼 텍스트 수정
                    }

                    Button(
                        onClick = { /*TODO*/ },
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
    private fun BoxTransactionRowItem(transaction: BoxTransaction) {
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
