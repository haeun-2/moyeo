package com.d108.moyeo.presentation.ui.component.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.d108.moyeo.domain.model.stats.CategoryStat
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.primaryLight
import java.text.DecimalFormat

@Composable
fun HistoryItem(  // 히스토리 탭에서 하단에 레이지컬럼으로 표시될 카테고리별 사용량
    stat: CategoryStat
) {

    val formattedAmount = "${DecimalFormat("#,###").format(stat.amount)}원"
    val formattedRatio = "${String.format("%.1f", stat.ratio)}%"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.Small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 좌측: 원그래프 (임시)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color = primaryLight)
        )

        Spacer(modifier = Modifier.width(Padding.HorizontalMedium))

        // 가운데: 품목 이름과 퍼센테이지
        Column {
            Text(text = stat.category, style = Typography.bodyMedium)
            Text(text = formattedRatio, style = Typography.bodyMedium)
        }

        // 우측: 지출액 (오른쪽 끝으로 밀어내기 위해 Spacer 사용)
        Spacer(Modifier.weight(1f))

        Text(text = formattedAmount, style = Typography.bodyMedium)
    }
}
