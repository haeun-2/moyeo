package com.d108.moyeo.presentation.ui.screen.signup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun TermsContent(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    Column {
        Text("약관에 동의해주세요.", style = Typography.titleLarge)
        Spacer(Modifier.height(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                // 전체 동의 상태를 사용하고, 클릭 시 onAllTermsChanged 함수 호출
                checked = uiState.allTermsAccepted,
                onCheckedChange = { viewModel.onAllTermsChanged(!uiState.allTermsAccepted) }
            )
            Text("전체 동의")
        }
        HorizontalDivider()
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                // 서비스 이용약관 상태를 사용하고, 클릭 시 onTermsOfServiceChanged 함수 호출
                checked = uiState.termsOfServiceAccepted,
                onCheckedChange = { viewModel.onTermsOfServiceChanged(!uiState.termsOfServiceAccepted) }
            )
            Text("서비스 이용약관 (필수)", modifier = Modifier.weight(1f))
            TextButton(
                onClick = { /*TODO*/ }
            ) { Text("보기") }
        }
        // TODO: 개인정보 처리방침 등 다른 약관들도 위와 같은 방식으로 추가
    }
}