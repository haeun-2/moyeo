package com.d108.moyeo.presentation.ui.component.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.errorLight
import com.d108.moyeo.presentation.theme.onSurfaceVariantLight
import com.d108.moyeo.presentation.theme.secondaryContainerLight
import com.d108.moyeo.presentation.theme.surfaceLight
import com.d108.moyeo.presentation.ui.screen.home.Notification

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 읽지 않은 알림은 다른 배경색으로 강조
    val backgroundColor = if (!notification.isRead) {
        secondaryContainerLight
    } else {
        Color.Transparent
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(horizontal = Spacing.SmallMedium, vertical = Spacing.Small)
    ) {
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
                text = notification.timestamp,
                style = Typography.bodySmall,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(Spacing.ExtraSmall))
        Text(
            text = notification.content,
            style = Typography.bodyMedium,
            color = onSurfaceVariantLight
        )
    }
}