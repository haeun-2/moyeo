package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.d108.moyeo.presentation.theme.*

@Composable
fun ReservationFinalCompleteScreen(
    navController: NavController,
    currencyCode: String,
    currencyName: String,
    targetRate: String,
    amount: String,
    startDate: String,
    endDate: String,
    boxId: Long,
    viewModel: ReservationFinalCompleteViewModel = hiltViewModel()
) {
    // boxId를 ViewModel에 전달
    LaunchedEffect(boxId, currencyCode, targetRate, amount, endDate) {
        viewModel.initialize(boxId, currencyCode, targetRate, amount, endDate)
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(Padding.Content),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // 완료 메시지
        Text(
            text = "환전 예약 완료!",
            style = Typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "설정하신 환율에 도달하면\n자동으로 환전 후 알려드립니다.",
            style = Typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 정보 카드들
        InfoCard(label = "통화", value = currencyName)
        Spacer(modifier = Modifier.height(12.dp))
        InfoCard(label = "지정 환율", value = "KRW $targetRate 이하일 때")
        Spacer(modifier = Modifier.height(12.dp))
        InfoCard(label = "예약 금액", value = "$currencyCode $amount")

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "내 지갑에 보유 중인 원화가 \n 출금되어 환전됩니다.",
            style = Typography.bodySmall,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        // 확인 버튼
        Button(
            onClick = {
                navController.navigate("exchange_reservation_home") {
                    popUpTo("exchange_reservation_home") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
        ) {
            Text(
                text = "확인",
                color = Color.White,
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun InfoCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = Typography.bodyMedium,
                color = Color.Black
            )
            Text(
                text = value,
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}