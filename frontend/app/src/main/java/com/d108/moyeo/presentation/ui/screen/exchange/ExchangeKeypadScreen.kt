package com.d108.moyeo.presentation.ui.screen.exchange

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.CustomKeypad
import com.d108.moyeo.presentation.ui.component.KeyMode
import com.d108.moyeo.presentation.ui.component.KeypadKey

@Composable
fun ExchangeKeypadScreen(
    navController: NavController,
    mode: String = "charge" // 기본값 charge 또는 "refund"
) {
    val context = LocalContext.current
    var inputAmount by remember { mutableStateOf("0") }

    val isChargeMode = mode == "charge"
    val screenTitle = if (isChargeMode) "충전하기" else "돌려받기"
    val actionText = if (isChargeMode) "충전할" else "돌려받을"
    val balanceText = if (isChargeMode) "보유 웨이 머니: 10,000 원 (초과하는 지불 충전)" else "보유 웨이 머니: 1,000 원"

    // 세로 방향 레이아웃, 화면 전체를 채우고 배경 흰색, 전체 패딩 16dp.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
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
                text = screenTitle,
                style = Typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            Box(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 일본 JPY 섹션
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 회색 원
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "일본 JPY",
                    style = Typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = if (isChargeMode) "100 엔 = 980 원" else "100 엔 = 980 원",
                    style = Typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 환율 히스토리 버튼
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "환율 히스토리",
                    style = Typography.bodySmall,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 안내 텍스트
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${actionText} 금액을 입력해주세요",
                style = Typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 대한민국 KRW 섹션
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 회색 원
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "대한민국 KRW",
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 입력 금액 표시
        Text(
            text = "$inputAmount 원",
            style = Typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 잔액 정보 (모드별 다른 텍스트)
        Text(
            text = balanceText,
            style = Typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        // 키패드 (CustomKeypad 사용)
        CustomKeypad(
            onKeyPress = { key ->
                when (key) {
                    is KeypadKey.Digit -> {
                        inputAmount =
                            if (inputAmount == "0") key.value.toString() else inputAmount + key.value.toString()
                    }

                    KeypadKey.Clear -> {
                        // KeyMode.Zeros일 때는 "00" 추가
                        inputAmount = if (inputAmount == "0") "00" else inputAmount + "00"
                    }

                    KeypadKey.Backspace -> {
                        inputAmount = if (inputAmount.length <= 1) "0" else inputAmount.dropLast(1)
                    }

                    is KeypadKey.Custom -> {
                        // 필요시 처리
                    }
                }
            },
            keypadColortype = "normal", // 일반 색상 사용
            keyMode = KeyMode.Zeros, // 00 버튼 활성화
            buttonAspectRatio = 1.2f,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .height(320.dp) // 키패드 높이 고정
        )
        Spacer(modifier = Modifier.weight(1f))
        // 하단 실행 버튼
        Button(
            onClick = {
                Toast.makeText(context, "$inputAmount 원 $screenTitle 실행됨", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = screenTitle,
                color = Color.White,
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}