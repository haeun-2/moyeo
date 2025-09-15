package com.d108.moyeo.presentation.ui.screen.signup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.errorLight
import com.d108.moyeo.presentation.theme.onSurfaceLight

@Composable
fun PhoneVerifyContent(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    Column {
        Text("인증번호를\n확인해주세요", style = Typography.titleLarge)
        Spacer(Modifier.height(20.dp))  // 후에 상수화 할 것
        OutlinedTextField(
            value = uiState.phoneCode,
            onValueChange = viewModel::onPhoneCodeChanged,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(text = "인증 번호를 입력해주세요",
                    style = Typography.bodyMedium,
                    color = onSurfaceLight
                )}, // 플레이스홀더 텍스트 설정
            shape = RoundedCornerShape(15.dp),  // 모서리를 둥글게 설정. 후에 상수화 할 것
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )
        Spacer(Modifier.height(20.dp))  // 후에 상수화 할 것
        Text(text = uiState.errorMessage?: "",
            style = Typography.bodyMedium,
            color = errorLight
        )
    }
}