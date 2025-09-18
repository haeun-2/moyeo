package com.d108.moyeo.presentation.ui.component.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.onSurfaceVariantLight
import com.d108.moyeo.presentation.theme.primaryLight
import com.d108.moyeo.presentation.theme.surfaceVariantLight

@Composable
fun HistoryBoxesItem(  // 모여박스 선택 화면
    box: Box,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val cardColors = CardDefaults.cardColors(
        containerColor = surfaceVariantLight,
        contentColor = onSurfaceVariantLight
    )

    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isSelected) BorderStroke(2.dp, primaryLight) else null,
        colors = cardColors
    ) {
        // 부모 요소를 꽉 채움
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.Medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽엔 원형 아이콘 적당히 넣어서 모여 박스 대표 프사가 들어갈 공간
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox, // 임시 아이콘
                    contentDescription = "모여박스 아이콘",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(Spacing.Small))

            // 오른쪽엔 이 모여 박스의 이름. 이 모여 박스의 이름이 남은 공간을 모두 차지함
            Text(
                text = box.name,
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}