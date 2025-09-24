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
import java.util.Locale

@Composable
fun ReservationAmountInputScreen(
    navController: NavController,
    currencyCode: String,
    currencyName: String,
    targetRate: Long,
    viewModel: ReservationAmountInputViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(currencyCode, currencyName, targetRate) {
        viewModel.initialize(currencyCode, currencyName, targetRate)
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
                text = "예약하기",
                style = Typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 알림 메시지
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color.Red, RoundedCornerShape(3.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$currencyCode 원율이 KRW $targetRate 이하일 때",
                        style = Typography.bodySmall,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "환전할 금액을 입력해 주세요",
                    style = Typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 입력 금액 표시 및 통화 토글 컨테이너 (수평 정렬)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 입력 금액 표시 (큰 텍스트)
            Text(
                text = "${if (uiState.inputAmount == "0" && uiState.selectedTab == uiState.currencyCode) "" else uiState.inputAmount} ${uiState.selectedTab}",
                style = Typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            // 탭 버튼들 (토글)
            Row(
                modifier = Modifier
                    .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .padding(4.dp)
            ) {
                // Button for the foreign currency (e.g., JPY, CAD)
                Button(
                    onClick = { viewModel.selectTab(uiState.currencyCode) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.selectedTab == uiState.currencyCode) Color.White else Color.Transparent,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(uiState.currencyCode, style = Typography.bodySmall)
                }
                // Button for KRW
                Button(
                    onClick = { viewModel.selectTab("KRW") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.selectedTab == "KRW") Color.White else Color.Transparent,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("KRW", style = Typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 환산 금액 표시
        val krwAmountLong = uiState.krwValue.toLong()
        val foreignAmountDouble = uiState.foreignValue

        val convertedText = when {
            uiState.selectedTab == uiState.currencyCode -> {
                if (uiState.krwValue != 0.0) String.format(Locale.KOREA, "약 %,d원", krwAmountLong) else ""
            }
            uiState.selectedTab == "KRW" -> {
                if (uiState.foreignValue != 0.0) String.format(Locale.US, "약 %,.2f %s", foreignAmountDouble, uiState.currencyCode) else ""
            }
            else -> ""
        }

        if (convertedText.isNotBlank()) {
            Text(
                text = convertedText,
                style = Typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.weight(1f))

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
            modifier = Modifier.height(320.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 다음 버튼
        Button(
            onClick = {
                val amountToPass = if (uiState.selectedTab == "KRW") {
                    uiState.foreignValue.toString()
                } else {
                    uiState.inputAmount
                }
                navController.navigate("reservation_period_selection/$currencyCode/$currencyName/$targetRate/$amountToPass")
            },
            enabled = uiState.inputAmount != "0",
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (uiState.inputAmount != "0") MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
            )
        ) {
            Text(
                text = "다음",
                color = Color.White,
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
