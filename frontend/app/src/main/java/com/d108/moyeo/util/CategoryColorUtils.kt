package com.d108.moyeo.util

import androidx.compose.ui.graphics.Color
import com.d108.moyeo.domain.model.stats.CategoryStat
import com.d108.moyeo.presentation.theme.chartColors


object CategoryColorUtils {


    fun getColorForCategory(categoryName: String): Color {
        val hash = categoryName.hashCode()
        val index = kotlin.math.abs(hash) % chartColors.size
        return chartColors[index]
    }

    /**
     * 특정 카테고리가 메인 차트에서 시작하는 각도를 계산
     * @param stats 전체 통계 리스트 (메인 차트와 동일한 순서)
     * @param targetCategory 각도를 구하고 싶은 카테고리명
     * @return 시작 각도 (-90도 기준, 12시 방향부터)
     */
    fun getStartAngleForCategory(stats: List<CategoryStat>, targetCategory: String): Float {
        var currentAngle = -90f // 12시 방향부터 시작

        for (stat in stats) {
            if (stat.category == targetCategory) {
                return currentAngle
            }
            val sweepAngle = (stat.ratio.toFloat() / 100f) * 360f
            currentAngle += sweepAngle
        }

        return -90f // 찾지 못한 경우 기본값
    }

    fun getChartColors() = chartColors
}