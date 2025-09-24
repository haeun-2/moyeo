package com.d108.moyeo.presentation.ui.screen.exchange

import android.R.attr.text
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.data.remote.dto.exchange.ExchangeReservationResponseDto
import com.d108.moyeo.presentation.theme.*
import kotlin.collections.isNotEmpty

@Composable
fun ExchangeReservationHomeScreen(
    navController: NavController,
    viewModel: ExchangeReservationHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadReservations(boxId = 1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(Padding.Content)
    ) {
        // 상단 헤더 (동일)
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
            text = "유효 기간 최대 0일",
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

        // 등록된 항목 보기
        TextButton(
            onClick = { /* 등록된 항목 보기 */ }
        ) {
            Text(
                text = "등록된 항목 보기 >",
                color = Color.Gray,
                style = Typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 완료된 예약 목록 표시
        if (uiState.reservations.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.reservations) { reservation ->
                    ReservationItem(reservation = reservation)
                }
            }
        } else {
            // 빈 상태
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "새로운 예약을 등록해 주세요.",
                    style = Typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun ReservationItem(reservation: ExchangeReservationResponseDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.Green, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = getCurrencyName(reservation.toCurrency), // toCurrency 사용
                    style = Typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "완료",
                    style = Typography.bodySmall,
                    color = Color.Green,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "지정 환율  KRW ${reservation.targetRate.toInt()} 이하일 때",
                style = Typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "예약 금액  ${reservation.toCurrency} ${(reservation.amount / reservation.targetRate).toInt()}", // 계산 방식 수정
                style = Typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "원화 금액  KRW ${reservation.amount.toInt()}", // 실제 원화 금액
                style = Typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "설정 기간  ${reservation.createdAt.substring(0, 10)} - ${reservation.expiresAt}",
                style = Typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

// 확장 함수들을 파일 하단에 추가
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