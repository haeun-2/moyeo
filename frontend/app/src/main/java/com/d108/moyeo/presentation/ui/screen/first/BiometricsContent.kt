package com.d108.moyeo.presentation.ui.screen.first

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun BiometricsContent(viewModel: FirstViewModel) {
    Column {
        Text("모여를 사용하기 위해\n인증해주세요", style = Typography.titleLarge)

        Spacer(Modifier.weight(1f))  // 후에 상수화 할 것

    }
}