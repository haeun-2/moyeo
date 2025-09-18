package com.d108.moyeo.presentation.ui.screen.history

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.runtime.LaunchedEffect
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.ui.component.common.DateRangePickerModal
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
                viewModel.onBoxSelected(newId)
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


    Column(
        modifier = Modifier.padding(
            start = Padding.HorizontalLarge, end = Padding.HorizontalLarge,
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text =
                        if (uiState.selectedBox != null)
                            "${uiState.selectedBox!!.name}의 통계"
                        else
                            "통계를 볼 박스를 선택해주세요",
                    style = Typography.titleLarge,
                    modifier = Modifier.padding(top = Spacing.Small)
                )

                Spacer(modifier = Modifier.width(Spacing.Small))

                Icon(
                    imageVector = Icons.Default.PlayArrow,
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
                    val options = listOf("전체", "일자")
                    SingleChoiceSegmentedButtonRow {
                        options.forEachIndexed { index, label ->
                            SegmentedButton (
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                                onClick = { viewModel.onToggleChanged(index) },
                                selected = index == selectedIndex
                            ) {
                                Text(label)
                            }
                        }
                    }


                    // 콤보박스 메뉴가 있어서, 클릭하면 드랍다운 메뉴가 열리고 화폐를 선택할 수 있음
                    var isMenuExpanded by remember { mutableStateOf(false) }
                    val currencyOptions = listOf("KRW", "USD", "JPY", "EUR")
                    var selectedCurrency by remember { mutableStateOf(currencyOptions[0]) }

                    ExposedDropdownMenuBox(
                        expanded = isMenuExpanded,
                        onExpandedChange = { isMenuExpanded = it },
                    ) {
                        // 선택된 항목을 보여주는 텍스트 필드 부분
                        OutlinedTextField(
                            value = selectedCurrency,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "메뉴 열기")
                            },
                            modifier = Modifier
                                .menuAnchor() // 이 필드가 메뉴의 '기준점'임을 알립니다.
                                .width(100.dp),
                            textStyle = Typography.bodySmall,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                        // 실제 드롭다운 메뉴
                        ExposedDropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false }
                        ) {
                            currencyOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedCurrency = option
                                        isMenuExpanded = false
                                        // TODO: 나중에 viewModel.onCurrencySelected(option) 호출
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.SmallMedium))

            // 2. 원형 그래프 (임시 플레이스홀더)
            Box(
                modifier = Modifier
                    .size(214.dp)  // 이 크기를 상수화 할 것!!
                    .clip(CircleShape)
                    .background(surfaceLight),
                contentAlignment = Alignment.Center
            ) {
                Text("원형 그래프")
            }

            Spacer(modifier = Modifier.height(Spacing.Medium))

            // 3. 날짜 표시 영역
            val dateText = if (uiState.selectedToggleIndex == 0) {
                "전체 기간"
            } else {
                formatDateRange(uiState.startDateMillis, uiState.endDateMillis)
            }
            Text(
                text = dateText,
                style = Typography.bodyLarge,
                modifier = if (uiState.selectedToggleIndex == 1) {
                    Modifier.clickable(onClick = viewModel::onDateRangePickerClick)
                } else {
                    Modifier
                }
            )

            Spacer(modifier = Modifier.height(Spacing.Small))

            // 4. 지도보기 글자
            Text(
                text = "지도 보기",
                style = Typography.bodyLarge,
                modifier = Modifier.clickable {
                    Toast.makeText(context, "지도 보기(텍스트) 클릭됨", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(Spacing.Large))

            LazyColumn(
                modifier = Modifier.width(248.dp),
                verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
            ) {
                items(
                    // TODO: 이거 key를 뭘로 잡을지 고민하기. 나중 가면 카테고리는 고유할 것이기 떄문에 카테고리 고유로 해도 됨
                    // 그러니까 key = { it.category } 해도 될 거임 실제로는
                    items = uiState.historyItems,
                    key = { it.hashCode() }
                ) { statItem ->
                    HistoryItem(stat = statItem)
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

    val startDate = startMillis?.let { formatter.format(Date(it)) } ?: "날짜를 선택해주세요"
    val endDate = endMillis?.let { formatter.format(Date(it)) } ?: "종료일"

    // 시작일만 선택되었거나, 시작일과 종료일이 같은 경우
    if (endMillis == null || startMillis == endMillis) {
        return startDate
    }

    return "$startDate ~ $endDate"
}