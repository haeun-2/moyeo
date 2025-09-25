package com.d108.moyeo.presentation.ui.screen.home.transfer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.primaryLight
import java.text.DecimalFormat

@Composable
fun FinishContent(viewModel: TransferViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val groupBoxes = viewModel.selectableBoxes.collectAsState(initial = emptyList()).value

    val isDeposit = uiState.mode == TransferMode.DEPOSIT
    val titleText = if (isDeposit) "입금 완료!" else "이체 완료!"
    val tailVerb = if (isDeposit) " 를 입금했어요" else " 를 이체했어요"

    val targetBoxId = uiState.targetBox
    val targetBoxName = groupBoxes.find { it.id == targetBoxId }?.title ?: "알 수 없는 박스"

    // 금액에 천 단위 쉼표를 추가하기 위한 포맷터
    val formattedAmount = runCatching {
        DecimalFormat("#,###").format(uiState.howMuch.toLong())
    }.getOrElse { uiState.howMuch }

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
                titleText,
                style = Typography.titleLarge,
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
                    withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                        append(targetBoxName)
                    }
                    append(" (으)로")
                },
                style = Typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Medium,
                            color = primaryLight
                        )
                    ) {
                        append("$formattedAmount ${uiState.currency}")
                    }
                    append(tailVerb)
                },
                style = Typography.bodyMedium
            )
        }
    }
}