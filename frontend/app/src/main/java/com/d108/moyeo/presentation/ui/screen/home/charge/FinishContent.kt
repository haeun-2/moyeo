package com.d108.moyeo.presentation.ui.screen.home.charge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.d108.moyeo.presentation.theme.primaryLight
import java.text.DecimalFormat

@Composable
fun FinishContent(viewModel: ChargeViewModel) {

    val uiState by viewModel.uiState.collectAsState()
    // 금액에 천 단위 쉼표를 추가하기 위한 포맷터
    val formattedAmount = try {
        DecimalFormat("#,###").format(uiState.howMuch.toLong())
    } catch (e: NumberFormatException) {
        uiState.howMuch
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "충전 완료!",
                style = typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Medium,
                            color = primaryLight
                        )
                    ) {
                        append("$formattedAmount")
                    }
                    append(" 원이 충전되었어요")
                },
                style = typography.bodyMedium
            )
        }
    }
}