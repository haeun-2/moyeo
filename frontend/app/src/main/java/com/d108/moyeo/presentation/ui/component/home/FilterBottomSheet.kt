package com.d108.moyeo.presentation.ui.component.home

import android.R.attr.name
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Padding.HorizontalSmall
import com.d108.moyeo.presentation.theme.Padding.VerticalExtraSmall
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.onPrimaryContainerLight
import com.d108.moyeo.presentation.theme.onSurfaceLight
import com.d108.moyeo.presentation.theme.primaryContainerLight
import com.d108.moyeo.presentation.ui.screen.home.wallet.Period
import com.d108.moyeo.presentation.ui.screen.home.wallet.SortType
import com.d108.moyeo.presentation.ui.screen.home.wallet.WalletFilterOptions

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CommonFilterBottomSheet(
    initialFilters: FilterOptions,
    onConfirm: (FilterOptions) -> Unit,
    onDismiss: () -> Unit
) {
    // String 값을 꺼내 임시 상태
    var tempPeriod by remember { mutableStateOf(initialFilters.period) }
    var tempScope  by remember { mutableStateOf(initialFilters.scope) }
    var tempSort   by remember { mutableStateOf(initialFilters.sort) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HorizontalSmall, vertical = VerticalExtraSmall)
        ) {
            // 기간 설정
            SegmentedFilterSection(
                title = "기간",
                options = Period.entries.map { it.displayName },
                selectedOption = tempPeriod,
                onOptionSelected = { tempPeriod = it }
            )

            Spacer(Modifier.height(Spacing.Large))

            // 카테고리 설정
            CategorySegmentedPager(
                title = "카테고리",
                selected = tempScope,
                onSelected = { tempScope = it }
            )

            Spacer(Modifier.height(Spacing.Large))

            // 정렬 순 설정
            SegmentedFilterSection(
                title = "정렬",
                options = SortType.entries.map { it.displayName },
                selectedOption = tempSort,
                onOptionSelected = { tempSort = it }
            )

            Spacer(Modifier.height(Spacing.Large))

            // 버튼
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onConfirm(initialFilters.rebuild(tempPeriod, tempScope, tempSort))
                }
            ) { Text("확인", style = Typography.bodyMedium) }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// 각 필터 섹션을 위한 재사용 가능한 Composable
@Composable
private fun SegmentedFilterSection(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = Typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(Spacing.SmallMedium))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                val isSelected = selectedOption == option
                SegmentedButton(
                    // 각 항목들이 부모 영역을 등분해서 사용
                    modifier = Modifier.weight(1f),
                    shape = SegmentedButtonDefaults.itemShape(index, options.size),
                    onClick = { onOptionSelected(option) },
                    selected = isSelected,
                    // 선택된 버튼에만 색상을 적용
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = primaryContainerLight,
                        activeContentColor = onPrimaryContainerLight,
                        inactiveContainerColor = Color.Transparent,
                        inactiveContentColor = onSurfaceLight
                    ),
                    icon = {},
                    label = { Text(option) }
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CategorySegmentedPager(
    title: String,
    selected: String,
    onSelected: (String) -> Unit
) {

    // 페이지 구성 (1칸 / 3칸 / 3칸 / 3칸)
    val pages = FilterOptionData.scopePages

    // 현재 선택된 값이 속한 페이지로 초기 위치를 설정
    fun pageOf(value: String): Int =
        pages.indexOfFirst { it.contains(value) }.takeIf { it >= 0 } ?: 0

    var page by remember { mutableIntStateOf(pageOf(selected)) }
    var direction by remember { mutableIntStateOf(0) } // -1: left, +1: right

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 왼쪽 화살표
            IconButton(onClick = {
                direction = -1; page = (page - 1 + pages.size) % pages.size
            }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "이전") }

            Text(text = title, style = Typography.bodyMedium, fontWeight = FontWeight.SemiBold)

            // 오른쪽 화살표
            IconButton(onClick = {
                direction = 1; page = (page + 1) % pages.size
            }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "다음") }
        }

        Spacer(Modifier.height(Spacing.SmallMedium))

        // 가운데 슬라이드 애니메이션 영역
        AnimatedContent(
            targetState = page,
            transitionSpec = {
                if (direction >= 0) {
                    slideInHorizontally(tween(220)) { it } + fadeIn() togetherWith
                            slideOutHorizontally(tween(220)) { -it } + fadeOut()
                } else {
                    slideInHorizontally(tween(220)) { -it } + fadeIn() togetherWith
                            slideOutHorizontally(tween(220)) { it } + fadeOut()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { currentPage ->
            val options = pages[currentPage]
            // 균등 분할 세그먼트
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                options.forEachIndexed { index, label ->
                    val selectedHere = selected == label
                    SegmentedButton(
                        modifier = Modifier.weight(1f),
                        shape = SegmentedButtonDefaults.itemShape(index, options.size),
                        selected = selectedHere,
                        onClick = { onSelected(label); page = pageOf(label) }, // 선택이 바뀌면 페이지도 동기화
                        icon = {},
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = primaryContainerLight,
                            activeContentColor = onPrimaryContainerLight,
                            inactiveContainerColor = Color.Transparent,
                            inactiveContentColor = onSurfaceLight,
                        ),
                        label = { Text(label) }
                    )
                }
            }
        }
    }
}
