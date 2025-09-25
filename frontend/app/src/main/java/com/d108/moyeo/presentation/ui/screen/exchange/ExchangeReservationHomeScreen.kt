package com.d108.moyeo.presentation.ui.screen.exchange

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.data.remote.dto.exchange.ExchangeReservationResponseDto
import com.d108.moyeo.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeReservationHomeScreen(
    navController: NavController,
    viewModel: ExchangeReservationHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 에러 메시지 표시
    uiState.errorMessage?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading && isRefreshing) {
            isRefreshing = false
        }
    }

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
                text = "예약환전",
                style = Typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                color = Color.Black
            )
            IconButton(onClick = { /* 정보 표시 */ }) {
                Text("ⓘ", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "내 지갑에 보유 중인 원화가 출금되어 환전됩니다.",
            style = Typography.bodyMedium,
            color = Color.Gray
        )

        Text(
            text = "유효 기간 최대 6개월",
            style = Typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 환전 예약하기 버튼
        Button(
            onClick = {
                navController.navigate("currency_selection")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
        ) {
            Text(
                text = "환전 예약하기",
                color = Color.White,
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 등록된 항목 개수 표시
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "등록된 예약 (${uiState.reservations.size}개)",
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            TextButton(
                onClick = {
                    isRefreshing = true
                    viewModel.refreshReservations()
                }
            ) {
                Text(
                    text = if (isRefreshing) "새로고침중..." else "새로고침",
                    color = Color.Gray,
                    style = Typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 예약 목록 표시
        when {
            isRefreshing || (uiState.isLoading && uiState.reservations.isEmpty()) -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "예약 목록을 불러오는 중...",
                            style = Typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }

            uiState.reservations.isEmpty() && !uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "등록된 예약이 없습니다",
                            style = Typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "새로운 예약을 등록해 주세요.",
                            style = Typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }

            else -> {
                val sortedReservations = uiState.reservations.sortedBy { it.expiresAt }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(sortedReservations, key = { it.id }) { reservation: ExchangeReservationResponseDto ->
                        ReservationItem(
                            reservation = reservation,
                            onCancelClick = { reservationId ->
                                viewModel.cancelReservation(reservationId)
                            },
                            onHideClick = { reservationId ->
                                viewModel.hideReservation(reservationId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReservationItem(
    reservation: ExchangeReservationResponseDto,
    onCancelClick: (String) -> Unit,
    onHideClick: (String) -> Unit
) {
    var showHideDialog by remember { mutableStateOf(false) }

    if (showHideDialog) {
        AlertDialog(
            onDismissRequest = { showHideDialog = false },
            title = { Text("숨기기") },
            text = { Text("이 예약을 숨기시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onHideClick(reservation.id.toString())
                        showHideDialog = false
                    }
                ) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showHideDialog = false }) { Text("취소") }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { showHideDialog = true } // 길게 누르면 다이얼로그
                )
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                when (reservation.status.uppercase()) {
                                    "COMPLETED" -> Color.Blue
                                    "WAITING" -> Color.Green
                                    "CANCELLED" -> Color.Gray
                                    else -> Color.Red
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = getCurrencyName(reservation.toCurrency),
                        style = Typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }

                Text(
                    text = when (reservation.status.uppercase()) {
                        "COMPLETED" -> "완료"
                        "WAITING" -> "대기중"
                        "CANCELLED" -> "취소됨"
                        else -> reservation.status
                    },
                    style = Typography.bodySmall,
                    color = when (reservation.status.uppercase()) {
                        "COMPLETED" -> Color.Blue
                        "WAITING" -> Color.Green
                        "CANCELLED" -> Color.Gray
                        else -> Color.Red
                    },
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            ReservationInfoRow("지정 환율", "KRW ${reservation.targetRate.toInt()} 이하일 때")
            ReservationInfoRow(
                "예약 금액",
                "${reservation.toCurrency} ${reservation.amount.toDouble() / reservation.targetRate.toDouble()}"
            )
            ReservationInfoRow("원화 금액", "KRW ${reservation.amount}")
            ReservationInfoRow("만료 일자", reservation.expiresAt.substring(0, 10))

            Spacer(modifier = Modifier.height(12.dp))

            if (reservation.status.uppercase() == "PENDING") {
                OutlinedButton(
                    onClick = { onCancelClick(reservation.id.toString()) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Text("예약 취소", style = Typography.bodyMedium)
                }
            }
        }
    }
}


@Composable
private fun ReservationInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = Typography.bodySmall, color = Color.Gray)
        Text(value, style = Typography.bodySmall, color = Color.Black, fontWeight = FontWeight.Medium)
    }
    Spacer(modifier = Modifier.height(4.dp))
}

private fun getCurrencyName(currencyCode: String): String {
    return when (currencyCode) {
        "USD" -> "미국 달러"
        "EUR" -> "유럽 유로"
        "JPY" -> "일본 엔"
        "GBP" -> "영국 파운드"
        "CNY" -> "중국 위안"
        "CAD" -> "캐나다 달러"
        "AUD" -> "호주 달러"
        "CHF" -> "스위스 프랑"
        "HKD" -> "홍콩 달러"
        "SGD" -> "싱가포르 달러"
        "KRW" -> "한국 원"
        else -> "${currencyCode} 통화"
    }
}
