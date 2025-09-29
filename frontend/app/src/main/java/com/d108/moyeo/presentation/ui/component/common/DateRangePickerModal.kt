package com.d108.moyeo.presentation.ui.component.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.d108.moyeo.presentation.theme.onPrimaryLight
import com.d108.moyeo.presentation.theme.onSurfaceLight
import com.d108.moyeo.presentation.theme.onSurfaceVariantLight
import com.d108.moyeo.presentation.theme.outlineLight
import com.d108.moyeo.presentation.theme.primaryContainerLight
import com.d108.moyeo.presentation.theme.primaryLight
import com.d108.moyeo.presentation.theme.surfaceLight
import java.text.SimpleDateFormat
import java.util.*

/**
 * 커스텀 예쁜 날짜 범위 선택기
 */
@Composable
fun DateRangePickerModal(
    onDismiss: () -> Unit,
    onConfirm: (Long?, Long?) -> Unit
) {
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var startDate by remember { mutableStateOf<Calendar?>(null) }
    var endDate by remember { mutableStateOf<Calendar?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = surfaceLight
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // 헤더
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "기간 선택",
                        style = typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = onSurfaceLight
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "닫기",
                            tint = outlineLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 선택된 날짜 표시
                DateSelectionDisplay(startDate, endDate)

                Spacer(modifier = Modifier.height(24.dp))

                // 월 네비게이션
                MonthNavigation(
                    currentMonth = currentMonth,
                    onPreviousMonth = {
                        currentMonth = Calendar.getInstance().apply {
                            timeInMillis = currentMonth.timeInMillis
                            add(Calendar.MONTH, -1)
                        }
                    },
                    onNextMonth = {
                        currentMonth = Calendar.getInstance().apply {
                            timeInMillis = currentMonth.timeInMillis
                            add(Calendar.MONTH, 1)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 캘린더
                CustomCalendar(
                    currentMonth = currentMonth,
                    startDate = startDate,
                    endDate = endDate,
                    onDateClick = { clickedDate ->
                        when {
                            startDate == null -> {
                                // 첫 번째 클릭 - 시작일 설정
                                startDate = clickedDate
                                endDate = null
                            }
                            endDate == null -> {
                                when {
                                    isSameDay(clickedDate, startDate!!) -> {
                                        // 같은 날짜 두 번 클릭 - 종료일도 같은 날로 설정 (당일 선택)
                                        endDate = clickedDate
                                    }
                                    clickedDate.after(startDate) -> {
                                        // 시작일보다 늦은 날짜 - 종료일로 설정
                                        endDate = clickedDate
                                    }
                                    else -> {
                                        // 시작일보다 이른 날짜 - 새로운 시작일로 설정
                                        startDate = clickedDate
                                        endDate = null
                                    }
                                }
                            }
                            else -> {
                                // 이미 범위가 선택된 상태 - 새로운 시작일로 리셋
                                startDate = clickedDate
                                endDate = null
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 버튼들
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("취소")
                    }

                    Button(
                        onClick = {
                            onConfirm(
                                startDate?.timeInMillis,
                                endDate?.timeInMillis ?: startDate?.timeInMillis  // 종료일이 없으면 시작일과 같게
                            )
                            onDismiss()
                        },
                        enabled = startDate != null,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("확인")
                    }
                }
            }
        }
    }
}

@Composable
private fun DateSelectionDisplay(
    startDate: Calendar?,
    endDate: Calendar?
) {
    val dateFormat = remember { SimpleDateFormat("MM월 dd일", Locale.KOREAN) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = primaryContainerLight.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 시작일
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "시작일",
                        style = typography.labelMedium,
                        color = onSurfaceVariantLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = startDate?.let { dateFormat.format(it.time) } ?: "선택하세요",
                        style = typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = if (startDate != null) primaryLight
                        else outlineLight
                    )
                }

                // 구분선
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(outlineLight.copy(alpha = 0.3f))
                )

                // 종료일
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "종료일",
                        style = typography.labelMedium,
                        color = onSurfaceVariantLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = endDate?.let { dateFormat.format(it.time) }
                            ?: startDate?.let { dateFormat.format(it.time) }
                            ?: "선택하세요",
                        style = typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = if (endDate != null || startDate != null) primaryLight
                        else outlineLight
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthNavigation(
    currentMonth: Calendar,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val monthYearFormat = remember { SimpleDateFormat("yyyy년 MM월", Locale.KOREAN) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "이전 달",
                tint = primaryLight
            )
        }

        Text(
            text = monthYearFormat.format(currentMonth.time),
            style = typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = onSurfaceLight
        )

        IconButton(onClick = onNextMonth) {
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "다음 달",
                tint = primaryLight
            )
        }
    }
}

@Composable
private fun CustomCalendar(
    currentMonth: Calendar,
    startDate: Calendar?,
    endDate: Calendar?,
    onDateClick: (Calendar) -> Unit
) {
    val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")

    Column {
        // 요일 헤더
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(daysOfWeek) { day ->
                Text(
                    text = day,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center,
                    style = typography.labelMedium,
                    color = onSurfaceVariantLight,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 날짜들
        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentMonth.timeInMillis
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(rows * 7) { index ->
                if (index < firstDayOfWeek || index >= firstDayOfWeek + daysInMonth) {
                    Spacer(modifier = Modifier.size(40.dp))
                } else {
                    val dayOfMonth = index - firstDayOfWeek + 1
                    val dateCalendar = Calendar.getInstance().apply {
                        timeInMillis = currentMonth.timeInMillis
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }

                    DateCell(
                        day = dayOfMonth,
                        isSelected = isDateSelected(dateCalendar, startDate, endDate),
                        isInRange = isDateInRange(dateCalendar, startDate, endDate),
                        isStart = startDate?.let { isSameDay(dateCalendar, it) } ?: false,
                        isEnd = endDate?.let { isSameDay(dateCalendar, it) } ?: false,
                        onClick = { onDateClick(dateCalendar) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DateCell(
    day: Int,
    isSelected: Boolean,
    isInRange: Boolean,
    isStart: Boolean,
    isEnd: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isStart || isEnd -> primaryLight
        isInRange -> primaryLight.copy(alpha = 0.2f)
        else -> Color.Transparent
    }

    val textColor = when {
        isStart || isEnd -> onPrimaryLight
        isSelected -> primaryLight
        else -> onSurfaceLight
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = typography.bodyMedium.copy(
                fontWeight = if (isStart || isEnd) FontWeight.Bold else FontWeight.Normal
            ),
            color = textColor
        )
    }
}

private fun isDateSelected(date: Calendar, startDate: Calendar?, endDate: Calendar?): Boolean {
    return (startDate?.let { isSameDay(date, it) } ?: false) ||
            (endDate?.let { isSameDay(date, it) } ?: false)
}

private fun isDateInRange(date: Calendar, startDate: Calendar?, endDate: Calendar?): Boolean {
    if (startDate == null || endDate == null) return false
    return date.after(startDate) && date.before(endDate)
}

private fun isSameDay(date1: Calendar, date2: Calendar): Boolean {
    return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
            date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH) &&
            date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH)
}