package com.d108.moyeo.presentation.ui.component.more

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.secondaryLight

@Composable
fun NoticesItem(
    date: String,
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }  // 확장 축소
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)  // 화살표 각도

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded } // 전체 영역 클릭 시 상태 변경
            .padding(horizontal = Spacing.Medium, vertical = Spacing.Small) // 좌우, 상하 패딩
    ) {
        // 상단 날짜
        Text(
            text = date,
            style = Typography.bodySmall,
            color = secondaryLight
        )

        Spacer(modifier = Modifier.height(Spacing.Small))

        // 중간 공지 제목과 화살표 아이콘
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f) // 텍스트가 남은 공간을 모두 차지
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "확장/축소",
                modifier = Modifier.rotate(rotationAngle) // 각도 애니메이션 적용
            )
        }

        // 하단 공지 내용 (확장되었을 때만 보임)
        AnimatedVisibility(visible = isExpanded) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}