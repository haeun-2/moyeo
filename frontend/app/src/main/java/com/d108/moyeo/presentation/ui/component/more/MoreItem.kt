package com.d108.moyeo.presentation.ui.component.more

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MoreItem(
    text: String,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(72.dp)
            .padding(
                horizontal = 16.dp,
            ),
        verticalAlignment = Alignment.CenterVertically // 내부 요소들을 수직 중앙 정렬
    ) {
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
