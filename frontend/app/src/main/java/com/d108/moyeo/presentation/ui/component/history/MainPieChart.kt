package com.d108.moyeo.presentation.ui.component.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.d108.moyeo.domain.model.stats.CategoryStat
import com.d108.moyeo.util.CategoryColorUtils.getColorForCategory
import java.text.DecimalFormat

/**
 * 메인 화면 중앙에 표시될 큰 원형 차트
 * @param stats 카테고리별 통계 리스트
 * @param totalAmount 총 금액 (중앙에 표시)
 * @param currency 화폐 단위
 * @param size 차트 크기
 * @param strokeWidth 차트 두께
 */

@Composable
fun MainPieChart(
    stats: List<CategoryStat>,
    totalAmount: Double,
    currency: String = "원",
    size: Dp = 214.dp,
    strokeWidth: Dp = 24.dp
) {
    val formattedTotal = DecimalFormat("#,###").format(totalAmount)

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // 원형 차트 그리기
        Canvas(
            modifier = Modifier.size(size)
        ) {
            val canvasSize = this.size
            val radius = (canvasSize.minDimension - strokeWidth.toPx()) / 2f
            val center = Offset(canvasSize.width / 2, canvasSize.height / 2)
            val strokeWidthPx = strokeWidth.toPx()

            var currentAngle = -90f // 12시 방향부터 시작

            stats.forEachIndexed { index, stat ->
                val sweepAngle = (stat.ratio.toFloat() / 100f) * 360f
                val color = getColorForCategory(stat.category)  // 카테고리 이름에 따라 색 배정

                drawArc(
                    color = color,
                    startAngle = currentAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(
                        center.x - radius,
                        center.y - radius
                    ),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidthPx)
                )

                currentAngle += sweepAngle
            }
        }

        // 중앙에 총액 표시
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formattedTotal,
                style = typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
            Text(
                text = currency,
                style = typography.bodyMedium.copy(
                    fontSize = 16.sp
                )
            )
        }
    }
}

