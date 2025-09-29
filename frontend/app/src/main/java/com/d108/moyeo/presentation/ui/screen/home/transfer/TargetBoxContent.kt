package com.d108.moyeo.presentation.ui.screen.home.transfer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.d108.moyeo.core.BoxStoreUiState
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.primaryLight

@Composable
fun TargetBoxContent(
    selectedBoxId: Long,
    onBoxSelect: (Long) -> Unit,
    boxes: List<BoxStoreUiState> // textColor 가 추가된 모임 박스 목록
) {

    Column {
        Text(
            text = "박스를\n선택해주세요",
            style = Typography.titleLarge
        )
        Spacer(Modifier.height(Spacing.Large))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.Medium) // 카드 사이의 간격
        ) {
            items(boxes) { box ->
                val isSelected = (box.id == selectedBoxId)
                SelectableGroupBoxCard(
                    title = box.title,
                    bg = box.bg,
                    textColor = box.textColor,
                    isSelected = isSelected,
                    onClick = { onBoxSelect(box.id) }
                )
            }
        }
    }
}

@Composable
private fun SelectableGroupBoxCard(
    title: String,
    bg: Color,
    textColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick, // 카드 전체에 클릭 이벤트를 적용합니다.
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = bg,
            contentColor = textColor
        ),
        // isSelected가 true일 때만 테두리를 표시하여 하이라이트 효과를 줍니다.
        border = if (isSelected) {
            BorderStroke(4.dp, primaryLight)
        } else {
            null
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp) // 일정한 높이를 주어 버튼처럼 보이게 합니다.
                .padding(Spacing.Medium),
            verticalAlignment = Alignment.CenterVertically // 수직 중앙 정렬
        ) {
            Text(
                modifier = Modifier.weight(1f), // 남은 공간을 모두 차지
                text = title,
                style = Typography.headlineMedium, // 텍스트 스타일 조정
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start // 왼쪽 정렬
            )
        }
    }
}