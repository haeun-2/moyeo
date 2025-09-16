package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.primaryLight
import com.d108.moyeo.presentation.ui.component.CustomKeypad
import com.d108.moyeo.presentation.ui.component.KeyMode
import com.d108.moyeo.presentation.ui.component.KeypadKey

@Composable
fun ExchangeReservationScreen(
    navController: NavController,
    currencyCode: String,
    currencyName: String,
    viewModel: ExchangeReservationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 선택된 통화 정보 설정
    androidx.compose.runtime.LaunchedEffect(currencyCode, currencyName) {
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
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(Spacing.Large))

        // 타이틀 텍스트
        Text(
            text = "예약하고 싶은 환율을\n확인해주세요",
            style = Typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Spacing.ExtraLarge))

        // 선택된 통화 정보
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 국기 아이콘 (회색 원)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = viewModel.getCurrencyFlag(currencyCode),
                    style = Typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.width(Spacing.SmallMedium))

            Text(
                text = currencyName,
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Small))

        // 환율 정보
        Text(
            text = "${uiState.exchangeRate} = 1000 원",
            style = Typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(Spacing.Large))

        // 입력 금액 표시 영역
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (uiState.inputAmount == "0") "1" else uiState.inputAmount,
                style = Typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Start
            )
        }

        // 키패드
        CustomKeypad(
            onKeyPress = { key ->
                when (key) {
                    is KeypadKey.Digit -> viewModel.onDigitInput(key.value.toString())
                    KeypadKey.Clear -> viewModel.onDigitInput("00")
                    KeypadKey.Backspace -> viewModel.onBackspace()
                    is KeypadKey.Custom -> { /* 필요시 작성 */ }
                }
            },
            keypadColortype = "normal",
            keyMode = KeyMode.Zeros,
            buttonAspectRatio = 1.2f,
            modifier = Modifier.height(320.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.Large))

        // 다음으로 버튼
        Button(
            onClick = {
                navController.navigate("exchange_keypad/reservation?currencyCode=${currencyCode}&currencyName=${currencyName}")
            },
            enabled = uiState.inputAmount != "0",
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (uiState.inputAmount != "0") primaryLight else Color.Gray,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                disabledContentColor = Color.Gray
            )
        ) {
            Text(
                text = "다음으로",
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Medium))
    }
}