package com.d108.moyeo.presentation.ui.screen.currency.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun PeriodSelectionContent(
    paddingValues: PaddingValues,
    startDate: String,
    endDate: String,
    // [수정] ViewModel 함수를 직접 받는 대신, 클릭 이벤트만 전달하도록 변경
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
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
                selected = startDate.isNotEmpty(),
                onClick = onStartDateClick // [수정]
            )

            PeriodButton(
                text = endDate.ifEmpty { "종료일 선택" },
                selected = endDate.isNotEmpty(),
                onClick = onEndDateClick // [수정]
            )
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
            else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) { Text(text) }
}