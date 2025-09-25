package com.d108.moyeo.presentation.ui.component.exchange.history

import android.graphics.Canvas
import android.util.Log
import android.widget.TextView
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.d108.moyeo.R
import com.d108.moyeo.domain.model.exchange.history.ExchangeRateHistory
import com.d108.moyeo.domain.model.exchange.history.ExchangeVolumeHistory
import com.d108.moyeo.presentation.theme.Spacing
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.SimpleDateFormat
import java.util.*

private val TAG = "SyncedChart"

@Composable
fun ExchangeHistorySyncedCharts(
    rateData: List<ExchangeRateHistory>,
    volumeData: List<ExchangeVolumeHistory>,
    tradeMode: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var rateVolumeMapping by remember { mutableStateOf(emptyList<Pair<ExchangeRateHistory, ExchangeVolumeHistory?>>()) }

    val combinedChart = remember {
        CombinedChart(context).apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            isHighlightFullBarEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            drawOrder = arrayOf(CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE)

            legend.apply {
                isEnabled = true
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                yOffset = 40f
            }

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(true)
                gridColor = 0xFFE0E0E0.toInt()
                gridLineWidth = 0.5f
                granularity = 1f
                setLabelCount(6, false)
                textColor = 0xFF666666.toInt()
                yOffset = 20f
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = 0xFFE0E0E0.toInt()
                gridLineWidth = 0.5f
                setLabelCount(6, false)
                textColor = 0xFF666666.toInt()
                setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                xOffset = 10f
            }

            axisRight.apply {
                setDrawGridLines(false)
                axisMinimum = 0f
                textColor = 0xFF666666.toInt()
                setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                xOffset = 10f
            }

            setExtraOffsets(8f, 0f, 8f, 20f)

            setXAxisRenderer(
                MultiLineXAxisRenderer(
                    viewPortHandler,
                    xAxis,
                    getTransformer(YAxis.AxisDependency.LEFT)
                )
            )
        }
    }

    val customMarker = remember(rateVolumeMapping) {
        object : MarkerView(context, R.layout.marker_view) {
            override fun refreshContent(e: Entry?, highlight: Highlight?) {
                if (e != null) {
                    val index = e.x.toInt()
                    val mappedData = rateVolumeMapping.getOrNull(index)
                    if (highlight?.dataSetIndex == 0 && mappedData != null) {
                        val (rateInfo, volumeInfo) = mappedData
                        val rate = if (tradeMode == "buy") rateInfo.buyRate else rateInfo.sellRate
                        val volume = volumeInfo?.totalAmount ?: 0.0
                        Log.d(TAG, "Marker Rate touched -> Time: ${rateInfo.period}, Rate: ${rate}" +
                                ", Volume Time: ${volumeInfo?.recordedAt ?: "N/A"}, Volume: $volume")
                        findViewById<TextView>(R.id.tvTime)?.text = "시간: ${rateInfo.period}"
                        findViewById<TextView>(R.id.tvRate)?.text = "환율: $rate"
                        findViewById<TextView>(R.id.tvVolume)?.text = "거래량: $volume"
                    }
                }
                super.refreshContent(e, highlight)
            }

            override fun getOffset(): MPPointF {
                return MPPointF(-(width / 2f), -height.toFloat())
            }
        }
    }

    LaunchedEffect(rateData, volumeData, tradeMode) {
        Log.d(TAG, "📊 Data Update -> Rate: ${rateData.size}, Volume: ${volumeData.size}, Mode: $tradeMode")
        if (rateData.isEmpty()) {
            Log.w(TAG, "⚠️ Empty rate data received")
            combinedChart.data = null
            combinedChart.invalidate()
            return@LaunchedEffect
        }

        val sourceFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREAN)
        val ratesWithTime = rateData.mapNotNull { rate ->
            try {
                val time = sourceFormat.parse(rate.period)?.time
                if (time != null) Pair(time, rate) else null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse rate time: ${rate.period}", e)
                null
            }
        }

        val volumesWithTime = volumeData.mapNotNull { volume ->
            try {
                val time = sourceFormat.parse(volume.recordedAt)?.time
                if (time != null) Pair(time, volume) else null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse volume time: ${volume.recordedAt}", e)
                null
            }
        }

        if (ratesWithTime.isEmpty()) {
            Log.w(TAG, "⚠️ Incomplete rate data after parsing. Not updating chart.")
            combinedChart.data = null
            combinedChart.invalidate()
            return@LaunchedEffect
        }

        val newMapping = mutableListOf<Pair<ExchangeRateHistory, ExchangeVolumeHistory?>>()
        var volumeIndex = 0
        ratesWithTime.forEachIndexed { rateIndex, (rateTime, rate) ->
            var matchedVolume: ExchangeVolumeHistory? = null
            while (volumeIndex < volumesWithTime.size && volumesWithTime[volumeIndex].first < rateTime) {
                volumeIndex++
            }
            if (volumeIndex < volumesWithTime.size) {
                val (currentVolumeTime, currentVolume) = volumesWithTime[volumeIndex]
                if (kotlin.math.abs(rateTime - currentVolumeTime) <= 10 * 60 * 1000L) {
                    matchedVolume = currentVolume
                    volumeIndex++
                    Log.d(TAG, "🟢 Mapped -> Rate Index: $rateIndex, Rate Time: ${rate.period}, Volume Time: ${matchedVolume.recordedAt}")
                } else {
                    Log.d(TAG, "🟡 No Match -> Rate Index: $rateIndex, Rate Time: ${rate.period}")
                }
            } else {
                Log.d(TAG, "🔴 No More Volumes -> Rate Index: $rateIndex, Rate Time: ${rate.period}")
            }
            newMapping.add(Pair(rate, matchedVolume))
        }
        rateVolumeMapping = newMapping

        val rateEntries = rateVolumeMapping.mapIndexed { index, (rate, _) ->
            val rateValue = if (tradeMode == "buy") rate.buyRate else rate.sellRate
            Entry(index.toFloat(), rateValue.toFloat())
        }

        val volumeEntries = rateVolumeMapping.mapIndexed { index, (_, volume) ->
            BarEntry(index.toFloat(), volume?.totalAmount?.toFloat() ?: 0f)
        }

        val lineDataSet = LineDataSet(rateEntries, if (tradeMode == "buy") "매수율" else "매도율").apply {
            color = if (tradeMode == "buy") 0xFF4CAF50.toInt() else 0xFFF44336.toInt()
            setDrawValues(false)
            setDrawCircles(true)
            circleRadius = 3f
            setCircleColor(color)
            lineWidth = 2.5f
            setDrawFilled(false)
            axisDependency = YAxis.AxisDependency.LEFT
            highlightLineWidth = 2f
            setDrawHorizontalHighlightIndicator(false)
            isHighlightEnabled = true
        }

        val barDataSet = BarDataSet(volumeEntries, "거래량").apply {
            color = 0xFF2196F3.toInt()
            setDrawValues(false)
            axisDependency = YAxis.AxisDependency.RIGHT
            isHighlightEnabled = false
        }

        val yMin = rateEntries.minOfOrNull { it.y } ?: 0f
        val yMax = rateEntries.maxOfOrNull { it.y } ?: 0f
        val padding = if (yMax - yMin == 0f) 1f else (yMax - yMin) * 0.05f
        combinedChart.axisLeft.apply {
            axisMinimum = yMin - padding
            axisMaximum = yMax + padding
        }

        combinedChart.xAxis.valueFormatter = object : ValueFormatter() {
            private var lastFormattedDateStr: String = ""
            private val displayDateFormat = SimpleDateFormat("MM/dd", Locale.KOREAN)
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                if (index < 0 || index >= rateData.size) return ""
                val currentData = rateData[index]
                val currentDateStr = currentData.period.substring(0, 10)
                val currentTimeStr = currentData.period.substring(11, 16)
                return if (currentDateStr != lastFormattedDateStr) {
                    lastFormattedDateStr = currentDateStr
                    val dateObject = sourceFormat.parse(currentData.period)
                    if (dateObject != null) "${displayDateFormat.format(dateObject)}\n$currentTimeStr" else currentTimeStr
                } else currentTimeStr
            }
        }

        combinedChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null && h != null) {
                    val index = e.x.toInt()
                    if (index in rateVolumeMapping.indices) {
                        val (rateInfo, volumeInfo) = rateVolumeMapping[index]
                        val rate = if (tradeMode == "buy") rateInfo.buyRate else rateInfo.sellRate
                        val volume = volumeInfo?.totalAmount ?: 0.0
                        Log.d(TAG, "🎯 Selected -> Index: $index, Rate Time: ${rateInfo.period}, Rate: $rate, Volume Time: ${volumeInfo?.recordedAt ?: "N/A"}, Volume: $volume")
                    }
                }
            }

            override fun onNothingSelected() {
                Log.d(TAG, "🎯 Nothing selected")
            }
        })

        combinedChart.marker = customMarker
        combinedChart.data = CombinedData().apply {
            setData(LineData(lineDataSet))
            setData(BarData(barDataSet).apply { barWidth = 0.6f })
        }
        combinedChart.animateX(800)
        Log.d(TAG, "✅ Chart updated successfully")
    }

    AndroidView(factory = { combinedChart }, modifier = modifier.fillMaxWidth().height(500.dp))
    Spacer(modifier = Modifier.height(Spacing.ExtraLarge))
}

class MultiLineXAxisRenderer(viewPortHandler: ViewPortHandler, xAxis: XAxis, trans: Transformer) : XAxisRenderer(viewPortHandler, xAxis, trans) {
    override fun drawLabel(c: Canvas, formattedLabel: String, x: Float, y: Float, anchor: MPPointF, angleDegrees: Float) {
        val lines = formattedLabel.split("\n")
        for ((i, line) in lines.withIndex()) {
            val lineY = y + i * mAxisLabelPaint.textSize
            c.drawText(line, x, lineY, mAxisLabelPaint)
        }
    }
}
