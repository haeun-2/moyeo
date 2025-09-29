package com.d108.moyeo.presentation.ui.component.qr

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.d108.moyeo.core.BoxStoreUiState
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun SquareMoyeoBoxItem(
    box: BoxStoreUiState,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Card를 사용하여 각 아이템에 그림자 효과 및 모양 부여
    Card(
        modifier = Modifier
            .width(168.dp)
            .fillMaxHeight() // 높이는 부모(LazyRow)를 꽉 채우도록
            .clickable(onClick = onClick), // 클릭 이벤트 연결
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        // 선택되었을 때 테두리 하이라이트 효과
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        colors = CardDefaults.cardColors(
            containerColor = box.bg
        )

    ) {
        Column(
            modifier = Modifier.padding(Spacing.Medium)
        ) {
            Text(
                text = box.title, // 파라미터로 받은 데이터 사용
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = box.textColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            // 화폐 목록
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(box.balances) { balance ->
                    CurrencyItem(balance = balance, textColor = box.textColor)
                }
            }
        }
    }
}