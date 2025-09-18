package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.primaryLight
import com.d108.moyeo.presentation.theme.surfaceVariantLight
import com.d108.moyeo.presentation.theme.onSurfaceVariantLight
import com.d108.moyeo.presentation.theme.outlineLight
import com.d108.moyeo.presentation.theme.pink
import kotlin.math.max

@Composable
fun ExchangeHistoryScreen(
    navController: NavController,
    currencyCode: String = "화폐단위",
    currencyName: String = "국가 화폐명",
    mode: String = "charge",
    viewModel: ExchangeHistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 화면 초기화
    androidx.compose.runtime.LaunchedEffect(currencyCode, mode) {
        viewModel.loadData(currencyCode, mode)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(Padding.Content)
            .verticalScroll(rememberScrollState())
    ) {
        // 상단 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = currencyName,
                style = Typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 현재 환율 정보
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "현재 환율",
                style = Typography.bodyMedium,
                color = onSurfaceVariantLight
            )
            Text(
                text = uiState.currentRate,
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 환율 추이 섹션
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFFFF5F5),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(Spacing.Medium)
        ) {
            Column {
                Text(
                    text = "환율 추이",
                    style = Typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Text(
                    text = "최근 히스토리",
                    style = Typography.labelLarge,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(Spacing.Medium))

                // 라인 차트
                LineChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    dataPoints = listOf(1349f, 1365f, 1356f, 1343f, 1329f, 1346f, 1367f, 1379f),
                    labels = listOf("6월", "7월", "8월", "9월", "10월", "11월")
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 가격별 사용자 수 섹션
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "가격별 사용자 수",
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            // 구매/판매 토글 버튼
            var selectedTab by remember { mutableStateOf(if (mode == "charge") "구매" else "판매") }

            Row(
                modifier = Modifier
                    .background(
                        color = Color.Gray.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(Spacing.Medium)
                    )
                    .padding(2.dp)
            ) {
                listOf("구매", "판매").forEach { tab ->
                    Button(
                        onClick = { selectedTab = tab },
                        modifier = Modifier
                            .width(60.dp)
                            .height(32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == tab) Color.White else Color.Transparent,
                            contentColor = if (selectedTab == tab) Color.Black else Color.Gray
                        ),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Text(
                            text = tab,
                            style = Typography.labelLarge,
                            fontWeight = if (selectedTab == tab) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.Medium))

        // 막대 차트
        BarChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            dataPoints = listOf(30f, 45f, 60f, 75f, 80f, 65f, 50f, 40f, 35f, 25f, 20f, 15f),
            labels = listOf("1345", "1350", "1355", "1360", "1365", "1370", "1375", "1380", "1385", "1390", "1395", "1400")
        )

        Spacer(modifier = Modifier.height(Spacing.ExtraLarge))
        // 맨 아래로 내리기
        Spacer(modifier = Modifier.weight(1f))

        // 하단 버튼
        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryLight
            )
        ) {
            Text(
                text = "이전 화면으로",
                color = Color.White,
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun LineChart(
    modifier: Modifier = Modifier,
    dataPoints: List<Float>,
    labels: List<String>
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height - 40.dp.toPx() // 하단 여백

        if (dataPoints.isEmpty()) return@Canvas

        val maxValue = dataPoints.maxOrNull() ?: 1f
        val minValue = dataPoints.minOrNull() ?: 0f
        val range = maxValue - minValue

        val stepX = width / (dataPoints.size - 1)

        // 라인 그리기
        val path = Path()
        dataPoints.forEachIndexed { index, point ->
            val x = index * stepX
            val y = height - ((point - minValue) / range * height)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }

            // 포인트 그리기
            drawCircle(
                color = Color.Red,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )

            // 값 표시
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    "${point.toInt()}",
                    x,
                    y - 15.dp.toPx(),
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 10.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }

        // 라인 그리기
        drawPath(
            path = path,
            color = Color.Red,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}

@Composable
private fun BarChart(
    modifier: Modifier = Modifier,
    dataPoints: List<Float>,
    labels: List<String>
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height - 40.dp.toPx()

        if (dataPoints.isEmpty()) return@Canvas

        val maxValue = dataPoints.maxOrNull() ?: 1f
        val barWidth = width / dataPoints.size * 0.6f
        val barSpacing = width / dataPoints.size * 0.4f

        dataPoints.forEachIndexed { index, value ->
            val barHeight = (value / maxValue) * height
            val x = index * (barWidth + barSpacing) + barSpacing / 2
            val y = height - barHeight

            // 막대 그리기
            drawRect(
                color = primaryLight,
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
            )

            // 라벨 그리기
            if (index < labels.size) {
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        labels[index],
                        x + barWidth / 2,
                        height + 25.dp.toPx(),
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 8.sp.toPx()
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }
        }
    }
}