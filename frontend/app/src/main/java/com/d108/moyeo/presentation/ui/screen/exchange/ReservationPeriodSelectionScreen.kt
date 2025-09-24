package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Padding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun ReservationPeriodSelectionScreen(
    navController: NavController,
    currencyCode: String,
    currencyName: String,
    targetRate: Long,
    amount: String,
    viewModel: ReservationPeriodViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(Padding.Content)
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
            Text(
                text = "예약하기",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 제목
        Text(
            text = "예약 기간을 선택해 주세요.",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "최소 1일에서 최대 6개월까지 설정 가능합니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 기간 선택 버튼들
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PeriodButton(
                text = if (uiState.startDate.isNotEmpty()) {
                    formatDisplayDate(uiState.startDate)
                } else "시작일 선택",
                isSelected = uiState.startDate.isNotEmpty(),
                modifier = Modifier.weight(1f),
                onClick = { viewModel.showCalendar("start") }
            )
            PeriodButton(
                text = if (uiState.endDate.isNotEmpty()) {
                    formatDisplayDate(uiState.endDate)
                } else "종료일 선택",
                isSelected = uiState.endDate.isNotEmpty(),
                modifier = Modifier.weight(1f),
                onClick = { viewModel.showCalendar("end") }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 캘린더 바텀시트
        if (uiState.showCalendar) {
            DynamicCalendarBottomSheet(
                currentMode = uiState.calendarMode,
                selectedStartDate = uiState.startDate,
                selectedEndDate = uiState.endDate,
                onDateSelected = { date ->
                    viewModel.selectDate(date)
                },
                onDismiss = {
                    viewModel.hideCalendar()
                }
            )
        }

        // 확인 버튼
        Button(
            onClick = {
                if (uiState.startDate.isNotEmpty() && uiState.endDate.isNotEmpty()) {
                    navController.navigate("reservation_complete/$currencyCode/$currencyName/$targetRate/$amount/${uiState.startDate}/${uiState.endDate}")
                }
            },
            enabled = uiState.startDate.isNotEmpty() && uiState.endDate.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
            )
        ) {
            Text(
                text = "확인",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DynamicCalendarBottomSheet(
    currentMode: String,
    selectedStartDate: String,
    selectedEndDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()
    val maxDate = today.plusMonths(6)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .background(
                Color.White,
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            // 캘린더 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${currentMonth.year}년 ${currentMonth.monthValue}월",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Row {
                    IconButton(
                        onClick = {
                            if (currentMonth.isAfter(YearMonth.now())) {
                                currentMonth = currentMonth.minusMonths(1)
                            }
                        },
                        enabled = currentMonth.isAfter(YearMonth.now())
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "이전 달",
                            tint = if (currentMonth.isAfter(YearMonth.now())) Color.Black else Color.Gray
                        )
                    }

                    IconButton(
                        onClick = {
                            if (currentMonth.isBefore(YearMonth.from(maxDate))) {
                                currentMonth = currentMonth.plusMonths(1)
                            }
                        },
                        enabled = currentMonth.isBefore(YearMonth.from(maxDate))
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = "다음 달",
                            tint = if (currentMonth.isBefore(YearMonth.from(maxDate))) Color.Black else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 요일 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 동적 날짜 그리드
            DynamicCalendarGrid(
                currentMonth = currentMonth,
                today = today,
                maxDate = maxDate,
                selectedStartDate = selectedStartDate,
                selectedEndDate = selectedEndDate,
                currentMode = currentMode,
                onDateSelected = onDateSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("취소", color = Color.Gray)
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("선택 완료", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun DynamicCalendarGrid(
    currentMonth: YearMonth,
    today: LocalDate,
    maxDate: LocalDate,
    selectedStartDate: String,
    selectedEndDate: String,
    currentMode: String,
    onDateSelected: (String) -> Unit
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val lastDayOfMonth = currentMonth.atEndOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()

    val weeks = mutableListOf<List<LocalDate?>>()
    var currentWeek = mutableListOf<LocalDate?>()

    // 빈 날짜들로 시작
    repeat(firstDayOfWeek) {
        currentWeek.add(null)
    }

    // 실제 날짜들 추가
    for (day in 1..daysInMonth) {
        val date = currentMonth.atDay(day)
        currentWeek.add(date)

        if (currentWeek.size == 7) {
            weeks.add(currentWeek)
            currentWeek = mutableListOf()
        }
    }

    // 마지막 주 완성
    if (currentWeek.isNotEmpty()) {
        while (currentWeek.size < 7) {
            currentWeek.add(null)
        }
        weeks.add(currentWeek)
    }

    Column {
        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                week.forEach { date ->
                    if (date != null) {
                        val isSelectable = !date.isBefore(today) && !date.isAfter(maxDate)
                        val isSelected = when (currentMode) {
                            "start" -> selectedStartDate == date.toString()
                            "end" -> selectedEndDate == date.toString()
                            else -> false
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .weight(1f)
                                .clickable(enabled = isSelectable) {
                                    onDateSelected(date.toString())
                                }
                                .background(
                                    when {
                                        !isSelectable -> Color.Transparent
                                        else -> Color.Transparent
                                    },
                                    RoundedCornerShape(20.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                color = when {
                                    isSelected -> Color.White
                                    !isSelectable -> Color.Gray.copy(alpha = 0.3f)
                                    else -> Color.Black
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected || date == today) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.2f),
            contentColor = if (isSelected) Color.White else Color.Gray
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun formatDisplayDate(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        "${date.monthValue}월 ${date.dayOfMonth}일"
    } catch (e: Exception) {
        dateString
    }
}