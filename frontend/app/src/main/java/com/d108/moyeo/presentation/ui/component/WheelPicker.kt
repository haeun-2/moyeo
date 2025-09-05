package com.d108.moyeo.presentation.ui.component.common

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.primaryLight
import kotlin.math.abs

@Composable
fun WheelPicker(
    items: List<String>,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 40.dp,
    onItemSelected: (String) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    val centralItemIndex by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) {
                -1
            } else {
                val viewportCenter = layoutInfo.viewportEndOffset / 2
                visibleItemsInfo.minByOrNull { abs(it.offset + it.size / 2 - viewportCenter) }?.index ?: -1
            }
        }
    }

    LaunchedEffect(centralItemIndex) {
        if (centralItemIndex != -1) {
            onItemSelected(items[centralItemIndex])
        }
    }

    Box(
        modifier = modifier.height(itemHeight * 3), // 중앙과 위아래 아이템이 보이도록 높이 설정
        contentAlignment = Alignment.Center
    ) {
        // 1. 스크롤되는 아이템 리스트
        LazyColumn(
            state = lazyListState,
            flingBehavior = snapFlingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items.size) { index ->
                val isSelected = index == centralItemIndex
                Text(
                    text = items[index],
                    style = if (isSelected) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier
                        .height(itemHeight)
                        .padding(vertical = 4.dp)
                        .graphicsLayer {
                            // 중앙에서 멀어질수록 작아지는 효과
                            val scale = if (isSelected) 1.0f else 0.8f
                            scaleX = scale
                            scaleY = scale
                        }
                )
            }
        }

        // 2. 선택 영역을 표시하는 상단/하단 라인
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(thickness = 2.dp, color = primaryLight)
            Spacer(modifier = Modifier.height(itemHeight))
            HorizontalDivider(thickness = 2.dp, color = primaryLight)
        }
    }
}