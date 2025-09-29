package com.d108.moyeo.presentation.ui.screen.home.wallet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.domain.model.history.HistoryTransaction
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.home.CategorySelectionBottomSheet
import com.d108.moyeo.presentation.ui.component.home.FilterOptionData
import java.text.DecimalFormat

private val TAG = "MyWalletDetailScreen"
@Composable
fun MyWalletDetailScreen(  // 각 아이템을 클릭했을 때 전환되는 화면
    navController: NavController,
    viewModel: MyWalletDetailViewModel = hiltViewModel() // ViewModel 주입
) {
    // ViewModel의 상태를 구독.
    val uiState by viewModel.uiState.collectAsState()
    val transaction = uiState.transaction

    // --- 카테고리 선택 바텀시트 호출 로직 ---
    if (uiState.showCategorySheet) {
        val unselectableCategories = setOf("전체", "입금", "출금", "환전")
        val selectableCategories = FilterOptionData.allScopeOptions.filter { it !in unselectableCategories }


        CategorySelectionBottomSheet(
            categories = selectableCategories,
            onCategorySelected = viewModel::onCategorySelected,
            onDismiss = viewModel::onCategorySheetDismiss
        )
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is MyWalletDetailNavEvent.NavigateBackWithSearchQuery -> {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("search_query", event.query)
                    navController.popBackStack()
                }
                is MyWalletDetailNavEvent.NavigateBackWithSearchCategory -> {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("search_category", event.category)
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateLeftPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateRightPadding(LocalLayoutDirection.current),
                )
        ) {
            if (transaction == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (transaction.category == "환전") {
                    ExchangeDetailContent(
                        transaction = transaction,
                        uiState = uiState,
                        viewModel = viewModel,
                        navController = navController
                    )
                } else {
                    GeneralTransactionDetailContent(
                        transaction = transaction,
                        uiState = uiState,
                        viewModel = viewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
private fun GeneralTransactionDetailContent(
    transaction: HistoryTransaction,
    uiState: MyWalletDetailUiState,
    viewModel: MyWalletDetailViewModel,
    navController: NavController
) {
    val isExpense = transaction.amount < 0
    val amountColor = if (isExpense) Color.Red else Color.Blue
    val formattedAmount = DecimalFormat("#,###.##").format(transaction.amount)
    val formattedBalance = DecimalFormat("#,###.##").format(transaction.balance)

    Column(
        modifier = Modifier.fillMaxSize().padding(Spacing.Large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(  // 스크롤 영역
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()), // 스크롤 기능 추가
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = transaction.title, style = Typography.titleLarge)
            Spacer(modifier = Modifier.height(Spacing.Medium))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(Spacing.Medium))

            DetailInfoRow(
                label = "카테고리",
                content = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = transaction.category, style = Typography.bodyLarge)
                        Spacer(modifier = Modifier.width(Spacing.Medium))
                        // 입출금이 아닐 때만 아이콘 보이도록
                        if (!(transaction.category == "입금" || transaction.category == "출금")) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "카테고리 수정",
                                modifier = Modifier.size(20.dp).clickable { viewModel.onCategoryEditClick() }
                            )
                        }
                    }
                }
            )
            DetailInfoRow(label = "거래시각", content = { Text(transaction.datetime, style = Typography.bodyLarge) })
            DetailInfoRow(label = "거래 금액", content = { Text("$formattedAmount ${transaction.currency}", style = Typography.bodyLarge, color = amountColor) })
            DetailInfoRow(label = "거래 후 잔액", content = { Text("$formattedBalance ${transaction.currency}", style = Typography.bodyLarge) })

            InlineEditMemoRow(
                memo = uiState.editedMemo,
                isEditing = uiState.isMemoEditing,
                onMemoChanged = viewModel::onMemoChanged,
                onEditClick = viewModel::startEditingMemo,
                onSaveClick = viewModel::saveMemoEdit,
                onCancelClick = viewModel::cancelMemoEdit
            )
            Spacer(modifier = Modifier.height(Spacing.Large))
            HorizontalDivider()
            SearchActionRow(text = "\"${transaction.title}\" 검색하기", onClick = viewModel::onSearchTitleClick)
            HorizontalDivider()
            SearchActionRow(text = "\"${transaction.category}\" 카테고리 검색하기", onClick = viewModel::onSearchCategoryClick)

        }

        Spacer(modifier = Modifier.height(Spacing.Medium))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("확인")
        }
    }
}

/**
 * '환전' 거래 내역을 위한 UI
 */
@Composable
private fun ExchangeDetailContent(
    transaction: HistoryTransaction,
    uiState: MyWalletDetailUiState,
    viewModel: MyWalletDetailViewModel,
    navController: NavController
) {
    val isExpense = transaction.amount < 0
    val exchangeDetail = uiState.exchangeDetails
    val formattedAmount = DecimalFormat("#,###.##").format(transaction.amount)
    val formattedBalance = DecimalFormat("#,###.##").format(transaction.balance)
    val amountColor = if (isExpense) Color.Red else Color.Blue

    Column(
        modifier = Modifier.fillMaxSize().padding(Spacing.Large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()), // 스크롤 기능 추가
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = transaction.title, style = Typography.titleLarge)
            Spacer(modifier = Modifier.height(Spacing.Medium))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(Spacing.Medium))

            // 기본 정보 (API 호출과 무관하게 즉시 표시)
            DetailInfoRow(label = "거래 시각", content = { Text(transaction.datetime, style = Typography.bodyLarge) })
            DetailInfoRow(label = "거래 금액", content = { Text("$formattedAmount ${transaction.currency}", style = Typography.bodyLarge, color = amountColor) })
            DetailInfoRow(label = "거래 후 잔액", content = { Text("$formattedBalance ${transaction.currency}", style = Typography.bodyLarge) })

            Spacer(modifier = Modifier.height(Spacing.Medium))
            HorizontalDivider()

            // 추가 정보 (API 호출 상태에 따라 표시)
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.Medium),
                contentAlignment = Alignment.Center
            ) {
                when {
                    // 1. API 호출 중 (로딩)
                    uiState.isLoading -> CircularProgressIndicator()
                    // 2. API 호출 성공
                    exchangeDetail.isNotEmpty() -> Column {
                        exchangeDetail.forEachIndexed { index, detail ->
                            if (index > 0) {
                                Spacer(modifier = Modifier.height(Spacing.Medium))
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = Spacing.Medium),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = "${index + 1}차 환전",
                                        style = Typography.bodyLarge,
                                        color = Color.Gray
                                    )

                                    Spacer(modifier = Modifier.height(Spacing.Medium))

                                    // 적용 환율에 표시될 숫자
                                    val leftCurrency: String
                                    val leftUnit: String
                                    val rightCurrency: String
                                    val rightUnit: String

                                    if (detail.fromCurrency == "KRW") {  // 한화에서 외화로 가는 경우
                                        if (detail.toCurrency == "JPY") {  // 한 -> 일화
                                            leftCurrency = "KRW"
                                            leftUnit = detail.exchangeRate.toString()
                                            rightCurrency = detail.toCurrency
                                            rightUnit = "100"
                                        } else {
                                            leftCurrency = "KRW"
                                            leftUnit = detail.exchangeRate.toString()
                                            rightCurrency = detail.toCurrency
                                            rightUnit = "1"
                                        }
                                    } else {  // 외화에서 한화로 가는 경우
                                        if (detail.fromCurrency == "JPY") {
                                            leftCurrency = "KRW"
                                            leftUnit = detail.exchangeRate.toString()
                                            rightCurrency = detail.fromCurrency
                                            rightUnit = "100"
                                        } else {
                                            leftCurrency = "KRW"
                                            leftUnit = detail.exchangeRate.toString()
                                            rightCurrency = detail.fromCurrency
                                            rightUnit = "1"
                                        }
                                    }

                                    Text(
                                        text = "$leftUnit $leftCurrency ≈ $rightUnit $rightCurrency",
                                        style = Typography.bodyLarge
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "- ${DecimalFormat("#,###.##").format(detail.fromAmount)} ${detail.fromCurrency}",
                                        style = Typography.bodyLarge,
                                        color = Color.Red,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Spacer(modifier = Modifier.height(Spacing.Small))

                                    Text("↓", style = Typography.bodyLarge)

                                    Spacer(modifier = Modifier.height(Spacing.Medium))

                                    Text(
                                        text = "+ ${DecimalFormat("#,###.##").format(detail.toAmount)} ${detail.toCurrency}",
                                        style = Typography.bodyLarge,
                                        color = Color.Blue,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                    // 3. API 호출 실패
                    uiState.errorMessage != null -> Text(uiState.errorMessage, color = Color.Red)
                }
            }

            HorizontalDivider()
            Spacer(modifier = Modifier.height(Spacing.Medium))

            // 메모 편집 기능
            InlineEditMemoRow(
                memo = uiState.editedMemo,
                isEditing = uiState.isMemoEditing,
                onMemoChanged = viewModel::onMemoChanged,
                onEditClick = viewModel::startEditingMemo,
                onSaveClick = viewModel::saveMemoEdit,
                onCancelClick = viewModel::cancelMemoEdit
            )

            Spacer(modifier = Modifier.height(Spacing.Medium))
            HorizontalDivider()
            SearchActionRow(text = "\"${transaction.title}\" 검색하기", onClick = viewModel::onSearchTitleClick)
            HorizontalDivider()
            SearchActionRow(text = "\"${transaction.category}\" 카테고리 검색하기", onClick = viewModel::onSearchCategoryClick)
        }

        Spacer(modifier = Modifier.height(Spacing.Medium))
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("확인")
        }
    }
}

/**
 * 환전 UI에서 From/To를 표시하기 위한 헬퍼 Composable
 */
@Composable
private fun CurrencyAmount(label: String, amount: String, currency: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = Typography.labelMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(Spacing.Small))
        Text(
            text = amount,
            style = Typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(text = currency, style = Typography.bodyMedium)
    }
}

// 정보 표시를 위한 재사용 가능한 Row Composable
@Composable
private fun DetailInfoRow(
    label: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = Typography.bodyLarge, color = Color.Gray)
        Spacer(modifier = Modifier.weight(1f))
        content()
    }
}

// 검색 액션을 위한 재사용 가능한 Row Composable
@Composable
private fun SearchActionRow(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.Large),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, style = Typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "이동"
        )
    }
}

// 간단한 텍스트 + 편집버튼 Row
@Composable
fun InlineEditMemoRow(
    memo: String,
    isEditing: Boolean,
    onMemoChanged: (String) -> Unit,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEditing) {
            // 편집 모드
            BasicTextField(
                value = memo,
                onValueChange = onMemoChanged,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                textStyle = Typography.bodyLarge.copy(color = Color.Black),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        if (memo.isEmpty()) {
                            Text(
                                text = "메모를 입력하세요",
                                style = Typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }
                }
            )
            // 저장/취소 버튼
            IconButton(onClick = onSaveClick) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "저장",
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = onCancelClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "취소",
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            // 표시 모드
            Text("메모")
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (memo.isNotEmpty()) memo else "메모 없음",
                style = Typography.bodyLarge,
                color = if (memo.isNotEmpty()) Color.Black else Color.Gray,
            )
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "메모 편집",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}