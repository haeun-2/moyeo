package com.d108.moyeo.presentation.ui.screen.home.transfer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.primaryLight
import java.text.DecimalFormat

@Composable
fun FinishContent(viewModel: SendingViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    // 금액에 천 단위 쉼표를 추가하기 위한 포맷터
    val formattedAmount = try {
        DecimalFormat("#,###").format(uiState.howMuch.toLong())
    } catch (e: NumberFormatException) {
        uiState.howMuch
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            "송금 완료!",
            style = typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(Spacing.Medium))

        // buildAnnotatedString을 사용하여 텍스트의 특정 부분만 강조합니다.
        Text(
            text = buildAnnotatedString {
                // TODO: targetBox ID를 실제 이름으로 변환하는 로직 필요
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("'${uiState.targetBox}'")
                }
                append("(으)로")
            },
            style = typography.bodyLarge
        )

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = primaryLight
                )
                ) {
                    append("$formattedAmount ${uiState.currency}")
                }
                append("를 보냈어요")
            },
            style = typography.bodyLarge
        )
    }
}