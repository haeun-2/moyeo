package com.d108.moyeo.presentation.ui.component.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 리스트 아이템용 작은 원형 차트
 * @param ratio 해당 카테고리의 비율 (0-100)
 * @param color 차트 색상
 * @param size 차트 크기
 * @param strokeWidth 차트 두께
 */
@Composable
fun SmallPieChart(
    ratio: Double,
    color: Color,
    size: Dp = 40.dp,
    strokeWidth: Dp = 6.dp,
    startAngle: Float = 90f
) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(size)
        ) {
            val canvasSize = this.size
            val radius = (canvasSize.minDimension - strokeWidth.toPx()) / 2f
            val center = Offset(canvasSize.width / 2, canvasSize.height / 2)
            val strokeWidthPx = strokeWidth.toPx()

            // 배경 원 (회색)
            drawArc(
                color = Color.Gray.copy(alpha = 0.2f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidthPx)
            )

            // 실제 비율 원호 (12시 방향부터 시계방향)
            val sweepAngle = (ratio.toFloat() / 100f) * 360f
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidthPx)
            )
        }
    }
}