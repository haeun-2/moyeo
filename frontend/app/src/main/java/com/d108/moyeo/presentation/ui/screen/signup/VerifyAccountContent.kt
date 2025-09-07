package com.d108.moyeo.presentation.ui.screen.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.onSurfaceLight
import com.d108.moyeo.presentation.theme.primaryLight

@Composable
fun VerifyAccountContent(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    Column {
        Text("해당 계좌로\n1원을 보냈어요", style = Typography.titleLarge)

        Spacer(Modifier.height(20.dp))  // 후에 상수화 할 것

        Box(modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd) {// 오른쪽 끝, 세로 중앙에 정렬

            OutlinedTextField(
                value = uiState.oneCoinNumber,
                onValueChange = viewModel::onOneCoinNumberChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = {
                    Text(
                        text = "입금자 명을 입력해주세요",
                        style = Typography.bodyMedium,
                        color = onSurfaceLight
                    )
                }, // 플레이스홀더 텍스트 설정
                shape = RoundedCornerShape(15.dp)  // 모서리를 둥글게 설정. 후에 상수화 할 것
            )
            Text(
                text = "3:00",
                modifier = Modifier.padding(end = 16.dp), // TextField의 테두리와 겹치지 않도록 패딩 추가
                style = Typography.bodyMedium,
                color = primaryLight // 타이머 색상을 강조색으로 설정
            )
        }
    }
}