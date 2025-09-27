package com.d108.moyeo.presentation.ui.screen.currency.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
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

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val todayMillis = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val startDateSelectableDates = remember {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= todayMillis
            }
        }
    }

    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = todayMillis,
        selectableDates = startDateSelectableDates
    )

    val endDateSelectableDates = remember(startDatePickerState.selectedDateMillis) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                startDatePickerState.selectedDateMillis?.let { startMillis ->
                    val sixMonthsLater = Calendar.getInstance().apply {
                        timeInMillis = startMillis
                        add(Calendar.MONTH, 6)
                    }.timeInMillis
                    return utcTimeMillis >= startMillis && utcTimeMillis <= sixMonthsLater
                }
                return utcTimeMillis >= todayMillis
            }
        }
    }

    val endDatePickerState = rememberDatePickerState(
        selectableDates = endDateSelectableDates
    )

    // 3. 시작 날짜 선택 다이얼로그
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate = convertMillisToDateString(startDatePickerState.selectedDateMillis)
                        onStartChange(selectedDate)
                        showStartDatePicker = false
                    },
                    enabled = startDatePickerState.selectedDateMillis != null
                ) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("취소") }
            }
        ) {
            DatePicker(
                state = startDatePickerState,
            )
        }
    }

    // 4. 종료 날짜 선택 다이얼로그
    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate = convertMillisToDateString(endDatePickerState.selectedDateMillis)
                        onEndChange(selectedDate)
                        showEndDatePicker = false
                    },
                    enabled = endDatePickerState.selectedDateMillis != null
                ) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("취소") }
            }
        ) {
            DatePicker(
                state = endDatePickerState,
            )
        }
    }

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
            ) { showStartDatePicker = true }

            PeriodButton(
                text = endDate.ifEmpty { "종료일 선택" },
                selected = endDate.isNotEmpty()
            ) { showEndDatePicker = true }
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

private fun convertMillisToDateString(millis: Long?): String {
    if (millis == null) return ""
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(millis))
}
