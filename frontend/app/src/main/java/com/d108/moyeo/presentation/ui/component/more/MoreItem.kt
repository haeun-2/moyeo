package com.d108.moyeo.presentation.ui.component.more

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.primaryLight

@Composable
fun MoreItem(
    // iconPainter: Painter, // 아이콘을 위한 Painter
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // 전체 Row를 클릭 가능하게
            .padding(
                horizontal = 16.dp, // 좌우 패딩 16dp (요구사항: 왼쪽에서 16dp, 대칭을 위해 오른쪽도 적용)
                vertical = 8.dp    // 상하 패딩 8dp
            ),
        verticalAlignment = Alignment.CenterVertically // 내부 요소들을 수직 중앙 정렬
    ) {
        Box(  // 후에 이미지로 대체해야 함
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color = primaryLight)
        )

        Spacer(modifier = Modifier.width(8.dp)) // 이미지와 텍스트 사이 8dp 여백

        Text(
            text = text  // 후에 글자 파라미터 받아야 함
        )

        // trailingContent가 있으면 오른쪽 끝에 배치
        if (trailingContent != null) {
            Spacer(Modifier.weight(1f))
            trailingContent()
        }
    }
}
