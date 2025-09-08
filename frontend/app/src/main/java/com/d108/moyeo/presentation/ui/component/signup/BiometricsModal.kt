package com.d108.moyeo.presentation.ui.component.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun BiometricsModal(
    onDismissRequest: () -> Unit, // 모달을 닫아야 할 때 호출될 함수 (외부 클릭 등)
    onSkipClicked: () -> Unit    // ▼▼▼ 모달의 "건너뛰기" 버튼을 위한 람다 추가 ▼▼▼
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 제목
                Text(
                    text = "생체 정보로 인증해주세요",
                    style = Typography.titleLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 지문 아이콘
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "지문 아이콘",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 내용
                Text(
                    text = "지문을 입력하세요",
                    style = Typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 건너뛰기 버튼
                TextButton(
                    onClick = onSkipClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("건너뛰기")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BiometricsModalPreview() {
    BiometricsModal(onDismissRequest = {}, onSkipClicked = {})
}