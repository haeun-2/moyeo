package com.d108.moyeo.presentation.ui.component.home.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.onSurfaceVariantLight
import com.d108.moyeo.presentation.ui.screen.home.NotificationItemUi

@Composable
fun NotificationItem(
    notification: NotificationItemUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.SmallMedium, vertical = Spacing.Small)
    ) {
        // 상단: 제목 - 수신시각
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = notification.title,
                style = Typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = notification.time ?: "",
                style = Typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.height(Spacing.ExtraSmall))

        // 본문: 보낸 사람 / 거래량 / 잔액 (존재하는 항목만)
        notification.sender?.let {
            Text(text = it, style = Typography.bodyMedium, color = onSurfaceVariantLight)
        }
        notification.amount?.let {
            Text(text = it, style = Typography.bodyMedium, color = onSurfaceVariantLight)
        }
        notification.balance?.let {
            Text(text = it, style = Typography.bodyMedium, color = onSurfaceVariantLight)
        }

        // 마지막 항목일 경우 HorizontalDivider 를 만들지 않음
        if (showDivider) {
            Spacer(modifier = Modifier.height(Spacing.Small))
            HorizontalDivider()
        }
    }
}
