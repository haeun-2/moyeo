package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.d108.moyeo.presentation.theme.*
import com.d108.moyeo.presentation.ui.component.CustomKeypad
import com.d108.moyeo.presentation.ui.component.KeyMode
import com.d108.moyeo.presentation.ui.component.KeypadKey

@Composable
fun ReservationRateInputScreen(
    navController: NavController,
    currencyCode: String,
    currencyName: String,
    viewModel: ReservationRateInputViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(currencyCode, currencyName) {
        viewModel.setCurrency(currencyCode, currencyName)
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
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 타이틀
        Text(
            text = "예약하고 싶은 환율을\n확인해주세요",
            style = Typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(15.dp))

        // 통화 정보
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = getCurrencyFlag(currencyCode))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = currencyName,
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 현재 환율
        Text(
            text = "KRW",
            style = Typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = uiState.currentRate,
            style = Typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 입력 환율 표시
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (uiState.inputRate == "0") "1" else uiState.inputRate,
                style = Typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        // 키패드
        CustomKeypad(
            onKeyPress = { key ->
                when (key) {
                    is KeypadKey.Digit -> viewModel.onDigitInput(key.value.toString())
                    KeypadKey.Clear -> viewModel.onDigitInput("00")
                    KeypadKey.Backspace -> viewModel.onBackspace()
                    else -> {}
                }
            },
            keypadColortype = "normal",
            keyMode = KeyMode.Zeros,
            buttonAspectRatio = 1.2f,
            modifier = Modifier.height(300.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 다음으로 버튼
        Button(
            onClick = {
                navController.navigate("reservation_amount_input/$currencyCode/$currencyName/${uiState.inputRate}")
            },
            enabled = uiState.inputRate != "0",
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
            )
        ) {
            Text(
                text = "다음으로",
                color = Color.White,
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
    }
}

private fun getCurrencyFlag(currencyCode: String): String {
    return when (currencyCode) {
        "USD" -> "🇺🇸"
        "EUR" -> "🇪🇺"
        "JPY" -> "🇯🇵"
        "GBP" -> "🇬🇧"
        "CNY" -> "🇨🇳"
        "CAD" -> "🇨🇦"
        "AUD" -> "🇦🇺"
        "CHF" -> "🇨🇭"
        "HKD" -> "🇭🇰"
        "SGD" -> "🇸🇬"
        "SEK" -> "🇸🇪"
        "NOK" -> "🇳🇴"
        "NZD" -> "🇳🇿"
        "THB" -> "🇹🇭"
        "VND" -> "🇻🇳"
        else -> "🏳️"
    }
}