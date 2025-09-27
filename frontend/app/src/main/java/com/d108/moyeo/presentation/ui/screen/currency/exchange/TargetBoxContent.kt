package com.d108.moyeo.presentation.ui.screen.currency.exchange

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
    boxes: List<BoxStoreUiState>,
    onNext: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "박스를\n선택해주세요",
            style = Typography.titleLarge
        )

        Spacer(Modifier.height(Spacing.Large))

        LazyColumn(
            modifier = Modifier
                .weight(1f)                   // 목록은 남는 공간 확장
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
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
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = bg,
            contentColor = textColor
        ),
        border = if (isSelected) BorderStroke(4.dp, primaryLight) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(Spacing.Medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = Typography.headlineMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start
            )
        }
    }
}
