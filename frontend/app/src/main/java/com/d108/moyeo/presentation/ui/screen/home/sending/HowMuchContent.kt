package com.d108.moyeo.presentation.ui.screen.home.sending

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.CustomKeypad
import com.d108.moyeo.presentation.ui.component.KeyMode
import com.d108.moyeo.presentation.ui.component.KeypadKey
import java.text.DecimalFormat

@Composable
fun HowMuchContent(viewModel: SendingViewModel) {

    val uiState by viewModel.uiState.collectAsState()

    // 입력된 숫자 문자열을 천 단위 쉼표가 있는 형식으로 변환합니다.
    // TODO: 이거 유틸로 뺄 수 있지 않나? 일단 보류
    val formattedAmount = remember(uiState.howMuch) {
        if (uiState.howMuch.isEmpty()) {
            "0"
        } else {
            try {
                DecimalFormat("#,###").format(uiState.howMuch.toLong())
            } catch (e: NumberFormatException) {
                "금액 초과" // 매우 큰 수가 입력되었을 때
            }
        }
    }

    Column {
        Text(
            text = "보낼 금액을\n입력해주세요",
            style = Typography.titleLarge,
        )

        Spacer(Modifier.height(Spacing.Large))

        Box(  // 보낼 금액이 뜰 영역
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$formattedAmount ${uiState.currency}",
                style = Typography.displayLarge
            )
        }

        Spacer(Modifier.height(Spacing.Large))

        CustomKeypad(keyMode = KeyMode.Zeros,  // 클리어 버튼 자리에 00이 들어옴
            keypadColortype = "normal",
            onKeyPress = { key ->
                when (key) {
                    is KeypadKey.Digit -> viewModel.onMoneyDigitInput(key.value.toString())
                    KeypadKey.Clear -> viewModel.onMoneyDigitInput("00")  // 클리어 버튼이 아니라 00 입력 버튼
                    KeypadKey.Backspace -> viewModel.onMoneyBackspace()
                    else -> {}
                }
            }
        )
    }
}