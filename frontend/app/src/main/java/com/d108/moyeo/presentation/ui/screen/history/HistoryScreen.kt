package com.d108.moyeo.presentation.ui.screen.history

import android.R.attr.onClick
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.surfaceLight
import com.d108.moyeo.presentation.ui.component.history.HistoryItem
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import com.d108.moyeo.domain.model.history.HistoryTransaction
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.backgroundLight
import com.d108.moyeo.presentation.theme.outlineLight
import com.d108.moyeo.presentation.theme.primaryLight
import com.d108.moyeo.presentation.theme.surfaceDimLight
import com.d108.moyeo.presentation.theme.surfaceVariantLight
import com.d108.moyeo.presentation.ui.component.common.DateRangePickerModal
import com.d108.moyeo.presentation.ui.component.history.CategoryHistoryBottomSheet
import com.d108.moyeo.presentation.ui.component.history.MainPieChart
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private val TAG = "HistoryScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController,
                  viewModel: HistoryViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val dateText = if (uiState.selectedToggleIndex == 0) {
        "전체 기간"
    } else {
        formatDateRange(uiState.startDateMillis, uiState.endDateMillis)
    }

    // 사용내역 클릭해서 화면 이동
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is HistoryNavEvent.NavigateToBoxSelection -> {
                    navController.navigate(AppScreen.HistoryBoxes.route)
                }
            }
        }
    }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getLiveData<Long>("selected_box_id_for_history")?.observeForever { newId ->
            if (newId != null) {
                viewModel.onBoxSelected(newId)  // 박스를 선택하고 돌아오면 상세 조회를 한다
                // 뷰모델의 loadCategoryStats 함수는 startDate와 endDate에 default로 빈 문자열("")을 전해주므로 전체 기간 조회가 된다


                Log.d(TAG, "HistoryScreen: 전달받은 박스 아이디: $newId")
                savedStateHandle.remove<Long>("selected_box_id_for_history")
            }
        }
    }

    if (uiState.showDateRangePicker) {
        DateRangePickerModal(
            onDismiss = viewModel::onDateRangePickerDismiss,
            onConfirm = viewModel::onDateRangeSelected
        )
    }

    if (uiState.selectedCategoryForSheet != null) {
        CategoryHistoryBottomSheet(
            categoryName = uiState.selectedCategoryForSheet!!.category,
            period = dateText, // 위에서 계산한 기간 텍스트
            totalAmount = uiState.selectedCategoryForSheet!!.amount,
            currency = uiState.selectedCurrency,
            groupedHistoryTransactions = uiState.groupedHistoryTransactions,
            onDismiss = viewModel::onBottomSheetDismiss
        )
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = surfaceLight
            )
    ) {
        Column(
            modifier = Modifier.padding(
                start = Padding.HorizontalMedium, end = Padding.HorizontalMedium,
                top = Padding.ScreenTop, bottom = Padding.ScreenBottom
            ),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center // 중앙 정렬
            ) {
                Row(
                    modifier = Modifier
                        .clickable {
                            viewModel.onSelectBoxClick()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text =
                            if (uiState.selectedBox != null)
                                "${uiState.selectedBox!!.name}의 통계"
                            else
                                "통계를 볼 박스를 선택해주세요",
                        style = Typography.titleLarge,
                    )

                    Spacer(modifier = Modifier.width(Spacing.Small))

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "상세보기로 이동"
                    )
                }
            }

            Spacer(modifier = Modifier.padding(top = Spacing.SmallMedium))  // 사용내역 헤더와 아래 박스 사이의 여백

            // 아래 큰 박스 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 토글 버튼
                        var selectedIndex = uiState.selectedToggleIndex
                        val options = listOf("전체", "기간")
                        SingleChoiceSegmentedButtonRow {
                            options.forEachIndexed { index, label ->
                                SegmentedButton(
                                    icon = {},
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = options.size
                                    ),
                                    onClick = { viewModel.onToggleChanged(index) },
                                    selected = index == selectedIndex
                                ) {
                                    Text(label)
                                }
                            }
                        }


                        val isDropdownEnabled = uiState.currencyOptions.isNotEmpty()
                        // currencyOptions가 비어있지 않을 때만 드롭다운 메뉴를 보여줍니다.
                        ExposedDropdownMenuBox(
                            expanded = if (isDropdownEnabled) uiState.isCurrencyMenuExpanded else false,
                            onExpandedChange = {
                                if (isDropdownEnabled) viewModel.onCurrencyMenuExpanded(it)
                            },
                        ) {
                            OutlinedTextField(
                                // 데이터 유무에 따라 표시할 텍스트 변경
                                value = if (isDropdownEnabled) uiState.selectedCurrency ?: "통화" else "내역 없음",
                                onValueChange = {},
                                readOnly = true,
                                // 데이터 없을 때 비활성화 상태로 만듦
                                enabled = isDropdownEnabled,
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "메뉴 열기"
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .width(120.dp),
                                textStyle = Typography.bodySmall,
                            )

                            if (isDropdownEnabled) {
                                ExposedDropdownMenu(
                                    expanded = uiState.isCurrencyMenuExpanded,
                                    onDismissRequest = { viewModel.onCurrencyMenuExpanded(false) }
                                ) {
                                    uiState.currencyOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                viewModel.onCurrencySelected(option)
                                                focusManager.clearFocus()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.SmallMedium))

                // 2. 원형 그래프
                if (uiState.currentStats?.content?.isNotEmpty() == true) {
                    val totalAmount = uiState.currentStats!!.content.sumOf { it.amount }
                    MainPieChart(
                        stats = uiState.currentStats!!.content,
                        totalAmount = totalAmount,
                        currency = uiState.selectedCurrency ?: "원"
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(214.dp)
                            .clip(CircleShape)
                            .background(surfaceLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("", style = Typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.Medium))

                // 3. 날짜 표시 영역

                Row(
                    // ✅ Row 자체에 Modifier를 적용합니다.
                    modifier = Modifier
                        .clip(RoundedCornerShape(50)) // 모서리를 둥글게 깎아 클릭 효과가 예쁘게 보이도록 함
                        .clickable(onClick = viewModel::onDateRangePickerClick)
                        .border(
                            width = 1.dp,
                            color = primaryLight,
                            shape = RoundedCornerShape(50)
                        )
                        // ✅ 패딩은 항상 적용하여 '전체'/'일자' 전환 시 UI가 출렁이지 않도록 합니다.
                        .padding(horizontal = Spacing.Medium, vertical = Spacing.Small),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = dateText,
                        style = Typography.bodyLarge,
                        color = primaryLight
                    )

                    if ( uiState.selectedToggleIndex == 1) {
                        Spacer(modifier = Modifier.width(Spacing.Small))
                        Icon(
                            imageVector = Icons.Default.DateRange, // 달력 아이콘
                            contentDescription = "날짜 선택",
                            tint = primaryLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.Large))

                // 5. 리스트 영역 - 상태에 따른 분기 처리
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else if (uiState.errorMessage != null) {
                    Text(text = uiState.errorMessage!!)
                } else if (uiState.selectedToggleIndex == 1 && !uiState.hasSelectedDateRange) {
                    // 일자 모드이면서 날짜를 선택하지 않은 경우
                    Text(
                        text = "",
                        style = Typography.bodyLarge
                    )
                } else if (uiState.currentStats?.content?.isEmpty() == true || uiState.currentStats == null) {
                    // 데이터는 있지만 거래 내역이 비어있는 경우
                    Text(
                        text = "거래 내역이 없습니다",
                        style = Typography.bodyLarge
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.width(248.dp),
                        verticalArrangement = Arrangement.spacedBy(1.dp) // 구분선 효과를 위해 간격 줄임
                    ) {
                        items(items = uiState.currentStats?.content ?: emptyList()) { statItem ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = surfaceLight
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                onClick = {
                                    viewModel.onHistoryItemClick(statItem)
                                }
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    HistoryItem(
                                        stat = statItem,
                                        allStats = uiState.currentStats?.content ?: emptyList(),
                                        currencyUnit = uiState.selectedCurrency
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}


/**
* Millis 값을 "yyyy년 M월 d일" 형식의 문자열로 변환하고, 범위를 예쁘게 표시하는 헬퍼 함수.
*/
@Composable
private fun formatDateRange(startMillis: Long?, endMillis: Long?): String {
    val formatter = remember { SimpleDateFormat("yyyy년 M월 d일", Locale.KOREAN) }

    val startDate = startMillis?.let { formatter.format(Date(it)) } ?: "기간을 선택해주세요"
    val endDate = endMillis?.let { formatter.format(Date(it)) } ?: "종료일"

    // 시작일만 선택되었거나, 시작일과 종료일이 같은 경우
    if (endMillis == null || startMillis == endMillis) {
        return startDate
    }

    return "$startDate ~ $endDate"
}