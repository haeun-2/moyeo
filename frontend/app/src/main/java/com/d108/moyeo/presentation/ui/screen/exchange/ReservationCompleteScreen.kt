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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.primaryLight
import com.d108.moyeo.presentation.theme.surfaceVariantLight

@Composable
fun ReservationCompleteScreen(
    navController: NavController,
    currencyCode: String,
    currencyName: String,
    foreignAmount: String,
    krwAmount: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(Padding.Content),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.weight(1f))

        // 완료 메시지
        Text(
            text = "충전 완료!",
            style = Typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.Medium))

        Text(
            text = "성공하신 환율에 도달하면\n자동으로 환전 후 알려드립니다.",
            style = Typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(Spacing.ExtraLarge))

        // 예약 정보 카드들
        ReservationInfoCard(
            label = "예약 수량 금액",
            value = "$foreignAmount ${getCurrencyUnit(currencyCode)}",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Spacing.Medium))

        ReservationInfoCard(
            label = "예약금",
            value = "${formatKrwAmount(krwAmount)} KRW",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Spacing.Medium))

        ReservationInfoCard(
            label = "잔액",
            value = "4,500 KRW",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // 확인 버튼
        Button(
            onClick = {
                // 환율 화면으로 돌아가기 (아니면 홈 이동으로 변경하기)
                navController.navigate("exchange") {
                    popUpTo("exchange") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = primaryLight
            )
        ) {
            Text(
                text = "확인",
                color = Color.White,
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Large))
    }
}

@Composable
private fun ReservationInfoCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = surfaceVariantLight,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = Spacing.Medium, vertical = Spacing.Medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = Typography.bodyMedium,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = value,
                style = Typography.bodyMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 통화 단위 가져오기
private fun getCurrencyUnit(currencyCode: String): String {
    return when (currencyCode) {
        "USD" -> "USD"
        "EUR" -> "EUR"
        "JPY" -> "JPY"
        "GBP" -> "GBP"
        "CNY" -> "CNY"
        "CAD" -> "CAD"
        "AUD" -> "AUD"
        "CHF" -> "CHF"
        "HKD" -> "HKD"
        "SGD" -> "SGD"
        "SEK" -> "SEK"
        "NOK" -> "NOK"
        "NZD" -> "NZD"
        "THB" -> "THB"
        "VND" -> "VND"
        "IDR" -> "IDR"
        "MYR" -> "MYR"
        "PHP" -> "PHP"
        "INR" -> "INR"
        "TWD" -> "TWD"
        "BRL" -> "BRL"
        "MXN" -> "MXN"
        "ZAR" -> "ZAR"
        "TRY" -> "TRY"
        "RUB" -> "RUB"
        else -> "JPY"
    }
}

// 원화 포맷팅 (천 단위 쉼표)
private fun formatKrwAmount(amount: String): String {
    return try {
        val number = amount.toLong()
        java.text.DecimalFormat("#,###").format(number)
    } catch (e: NumberFormatException) {
        amount
    }
}