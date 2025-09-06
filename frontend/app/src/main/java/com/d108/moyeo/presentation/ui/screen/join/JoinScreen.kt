package com.d108.moyeo.presentation.ui.screen.join

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun JoinScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Padding.HorizontalMedium, vertical = Padding.VerticalLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 컨텐츠 (환영 문구, 이미지, 설명)
        Column(
            modifier = Modifier.weight(1f), // 상단 영역이 남은 공간을 차지하도록
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "모여에 오신 것을 환영합니다!",
                style = Typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.ExtraLarge))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f) // 설명 영역보다 3배의 공간을 차지
                    .background(Color.LightGray.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text("적절한 이미지가 들어갈 공간 (3)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // [수정] 우리 앱에 대한 설명이 들어갈 공간 (비율: 1)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // 이미지 영역의 1/3 공간을 차지
                    .background(Color.Cyan.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("앱에 대한 설명 영역 (1)")
            }

            Spacer(modifier = Modifier.height(Spacing.ExtraLarge))
        }

        // 하단 컨텐츠 (버튼)
        Column {
            Button(
                onClick = { /* TODO: 회원가입 화면으로 이동 */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("회원가입 하기")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "이미 회원이신가요?",
                    style = MaterialTheme.typography.bodySmall
                )
                TextButton(onClick = { /* TODO: 로그인 화면으로 이동 */ }) {
                    Text("로그인")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FirstLoginScreenPreview() {
    val navController = rememberNavController()
    JoinScreen(navController = navController)
}
