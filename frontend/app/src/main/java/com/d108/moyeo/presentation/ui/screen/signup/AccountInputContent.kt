package com.d108.moyeo.presentation.ui.screen.signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.onSurfaceLight

@Composable
fun AccountInputContent(
    uiState: SignUpUiState,
    viewModel: SignUpViewModel,
    onBankFieldClick: () -> Unit) {
    Column {
        Text("연결할 계좌번호를\n입력해주세요", style = Typography.titleLarge)

        Spacer(Modifier.height(20.dp))  // 후에 상수화 할 것

        Box(
            modifier = Modifier.clickable(onClick = onBankFieldClick) // 클릭 이벤트를 Box로 옮겼습니다.
        ) {
            OutlinedTextField(
                value = uiState.accountBank,
                onValueChange = { }, // 직접 수정하지 않으므로 비워둠
                modifier = Modifier.fillMaxWidth(),
                readOnly = true, // 키보드가 올라오지 않도록 읽기 전용으로 설정
                enabled = false,  // 포커스 및 커서 깜빡임을 방지
                singleLine = true,
                placeholder = {
                    Text(text = "은행을 선택해주세요")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = onSurfaceLight,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                shape = RoundedCornerShape(15.dp)  // 모서리를 둥글게 설정. 후에 상수화 할 것
            )
        }


        Spacer(Modifier.height(20.dp))  // 후에 상수화 할 것

        OutlinedTextField(
            value = uiState.accountNumber,
            onValueChange = viewModel::onAccountNumberChanged,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = {
                Text(text = "계좌번호를 입력해주세요",
                    style = Typography.bodyMedium,
                    color = onSurfaceLight
                )},// 플레이스홀더 텍스트 설정
            shape = RoundedCornerShape(15.dp)  // 모서리를 둥글게 설정. 후에 상수화 할 것
        )
    }
}