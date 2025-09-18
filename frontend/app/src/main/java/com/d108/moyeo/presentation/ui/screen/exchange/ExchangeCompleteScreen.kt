package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.primaryLight
import java.text.DecimalFormat

@Composable
fun ExchangeCompleteScreen(
    navController: NavController,
    mode: String, // "charge" 또는 "refund"
    amount: String,
    currencyUnit: String
) {
    // 금액에 천 단위 쉼표를 추가하기 위한 포맷터
    val formattedAmount = try {
        DecimalFormat("#,###").format(amount.toLong())
    } catch (e: NumberFormatException) {
        amount
    }

    val title = when (mode) {
        "charge" -> "충전 완료!"
        "refund" -> "반환 완료!"
        else -> "완료!"
    }

    val actionText = when (mode) {
        "charge" -> "충전했어요"
        "refund" -> "반환했어요"
        else -> "완료했어요"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(Padding.Content),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.weight(1f))

        // 완료 제목
        Text(
            text = title,
            style = typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.Large))

        // 설명 텍스트
        Text(
            text = buildAnnotatedString {
                append("길동 님의 박스에")
            },
            style = typography.bodyLarge,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.Small))

        // 금액 강조 텍스트
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = primaryLight
                    )
                ) {
                    append("$formattedAmount $currencyUnit")
                }
                append("를 $actionText")
            },
            style = typography.bodyLarge,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        // 처음으로 돌아가기 버튼
        Button(
            onClick = {
                // 홈 화면으로 이동
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryLight
            )
        ) {
            Text(
                text = "처음으로 돌아가기",
                color = Color.White,
                style = typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Medium))
    }
}