package com.d108.moyeo.presentation.ui.component.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.secondaryLight

@Composable
fun MyConsultationItem(
    date: String,
    title: String,
    isAnswered: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // 전체 영역을 클릭 가능하게
            .padding(horizontal = Spacing.Medium, vertical = Spacing.Small)
    ) {
        // 상단 날짜
        Text(
            text = date,
            style = Typography.bodySmall,
            color = secondaryLight
        )

        Spacer(modifier = Modifier.height(Spacing.Small))

        // 중간 답변 여부 태그와 제목
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 답변 여부 아이콘(태그)
            val tagText = if (isAnswered) "답변완료" else "대기중"
            val tagBackgroundColor = if (isAnswered) MaterialTheme.colorScheme.primaryContainer else Color.LightGray
            val tagTextColor = if (isAnswered) MaterialTheme.colorScheme.onPrimaryContainer else Color.DarkGray

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(tagBackgroundColor)
                    .padding(horizontal = Spacing.ExtraSmall, vertical = Spacing.ExtraSmall)
            ) {
                Text(
                    text = tagText,
                    style = Typography.labelSmall,
                    color = tagTextColor
                )
            }

            Spacer(modifier = Modifier.width(Spacing.Small))

            // 제목
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f) // 남은 공간을 모두 차지
            )
        }
    }
}