package com.d108.moyeo.presentation.ui.screen.signup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.onSurfaceLight

@Composable
fun EmailVerifyContent(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    Column {
        Text("이메일을\n확인해주세요", style = Typography.titleLarge)
        Spacer(Modifier.height(20.dp))  // 후에 상수화 할 것

    }
}