package com.d108.moyeo.presentation.ui.screen.exchange.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PeriodSelectionContent(
    paddingValues: PaddingValues,
    startDate: String,
    endDate: String,
    onStartChange: (String) -> Unit,
    onEndChange: (String) -> Unit,
    isLoading: Boolean,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(20.dp)
    ) {
        Text("예약 기간을 선택해 주세요.", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(6.dp))
        Text("최소 1일 ~ 최대 6개월", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            PeriodButton(
                text = startDate.ifEmpty { "시작일 선택" },
                selected = startDate.isNotEmpty()
            ) { onStartChange(todayIso()) }

            PeriodButton(
                text = endDate.ifEmpty { "종료일 선택" },
                selected = endDate.isNotEmpty()
            ) { onEndChange(tomorrowIso()) }
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = onSubmit,
            enabled = startDate.isNotEmpty() && endDate.isNotEmpty() && !isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(22.dp))
            else Text("예약하기")
        }
    }
}

@Composable
private fun PeriodButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) { Text(text) }
}

// ✅ RequiresApi 제거, Calendar/DateFormat으로 대체
private fun todayIso(): String {
    val cal = Calendar.getInstance()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(cal.time)
}

private fun tomorrowIso(): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, 1)
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(cal.time)
}
