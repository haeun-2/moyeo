package com.d108.moyeo.presentation.ui.screen.exchange

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.CustomKeypad
import com.d108.moyeo.presentation.ui.component.KeyMode
import com.d108.moyeo.presentation.ui.component.KeypadKey
import com.d108.moyeo.util.CurrencyUtils

private val TAG = "ExchangeKeypadScreen"
@Composable
fun ExchangeKeypadScreen(
    navController: NavController,
    mode: String = "charge", // 기본값 charge, "refund", "reservation"
    currencyCode: String? = null,
    currencyName:String? = null,
    viewModel: ExchangeKeypadViewModel = hiltViewModel()
) {

    //mode: charge, refund, reservation
    val uiState by viewModel.uiState.collectAsState()

    Log.d(TAG, "currencyCode: $currencyCode")
    Log.d(TAG, "currencyName: $currencyName")
    Log.d(TAG, "mode: ${uiState.mode}")


    // 예약 모드일경우
    LaunchedEffect(mode, currencyCode, currencyName) {
        viewModel.setModeAndCurrency(mode, currencyCode, currencyName)
    }

    // 💡 [추가] 환율 정보를 로딩 중일 때 로딩 화면을 표시합니다.
    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // 세로 방향 레이아웃, 화면 전체를 채우고 배경 흰색, 전체 패딩 16dp
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
                text = viewModel.getScreenTitle(),
                style = Typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            Box(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(Spacing.ExtraLarge + Spacing.Small))

        // 일본 JPY
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 회색 원
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = CurrencyUtils.getCurrencyFlag(uiState.currencyCode ?: ""),
                    style = Typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.width(Spacing.Medium))

            Column {
                Text(
                    text = uiState.currencyName ?: "통화 정보 없음",
                    style = Typography.bodyLarge,
                    color = Color.Black
                )
                Text(
                    text = viewModel.getDisplayExchangeRate(),
                    style = Typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 환율 히스토리 버튼
            if (viewModel.shouldShowHistoryButton()) {
                Button(
                    onClick = {
                        // 환율 히스토리 화면으로 이동
                        val code = uiState.currencyCode
                        val name = uiState.currencyName
                        val currentMode = uiState.mode
                        navController.navigate("exchange_history/$code/$name/$currentMode")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(Spacing.Medium),
                    modifier = Modifier.height(Spacing.ExtraLarge)
                ) {
                    Text(
                        text = "환율 히스토리",
                        style = Typography.bodySmall,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.Medium))

        // 안내 텍스트
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${viewModel.getActionText()} 금액을 입력해주세요",
                style = Typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Medium))

        // 입력 금액 표시 (외국 통화)
        Text(
            text = "${uiState.inputAmount} ",
            style = Typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.Medium))

        // 대한민국 KRW 섹션
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 한국 국기
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "🇰🇷",
                    style = Typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.width(Spacing.Medium))

            Text(
                text = "대한민국 KRW",
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Medium))

        // 환산된 원화 금액 표시
        Text(
            text = "${uiState.convertedKrwAmount} 원",
            style = Typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 잔액 정보
        Text(
            text = viewModel.getBalanceText(),
            style = Typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp)
        )
        Spacer(modifier = Modifier.height(Spacing.Medium))

        // 키패드 (CustomKeypad 사용)
        CustomKeypad(
            onKeyPress = { key ->
                when (key) {
                    is KeypadKey.Digit -> viewModel.onDigitInput(key.value.toString())
                    KeypadKey.Clear -> viewModel.onDigitInput("00")
                    KeypadKey.Backspace -> viewModel.onBackspace()
                    is KeypadKey.Custom -> { /* 필요시 처리 */ }
                }
            },
            keypadColortype = "normal",
            keyMode = KeyMode.Zeros,
            buttonAspectRatio = 1.2f,
            modifier = Modifier
                .padding(horizontal = Spacing.Large)
                .height(320.dp)
        )
        Spacer(modifier = Modifier.weight(1f))

        // 하단 실행 버튼
        Button(
            onClick = {
                // 예약 모드일 때만 예약완료 페이지로 이동
                if (uiState.mode == "reservation"){
                    // 예약 완료 페이지로 이동 -> 변수 설정
                    val currencyCode = uiState.currencyCode
                    val currencyName = uiState.currencyName
                    val inputAmount = uiState.inputAmount
                    val convertedAmount = uiState.convertedKrwAmount
                    navController.navigate("reservation_complete/$currencyCode/$currencyName/$inputAmount/$convertedAmount")
                } else {
                    // 일반 충전/환불 모드일 때는 완료 페이지로 이동 -> 변수 설정
                    val mode = uiState.mode
                    val inputAmount = uiState.inputAmount
                    val currencyUnit = "abc"
                    navController.navigate("exchange_complete/$mode/$inputAmount/$currencyUnit")
                }
            },
            enabled = !uiState.isProcessing && uiState.inputAmount != "0",
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            if (uiState.isProcessing) {
                Text("처리 중...", color = Color.White, style = Typography.bodyLarge, fontWeight = FontWeight.Bold)
            } else {
                Text(
                    text = viewModel.getScreenTitle(),
                    color = Color.White,
                    style = Typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.Medium))
    }
}