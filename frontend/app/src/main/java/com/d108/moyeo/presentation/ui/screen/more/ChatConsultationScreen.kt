package com.d108.moyeo.presentation.ui.screen.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun ChatConsultationScreen(navController: NavController) {
    var subject by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = Spacing.Medium, end = Spacing.Medium,
                top = Padding.ScreenTop, bottom = Padding.ScreenBottom
            ),
    ) {
        // --- 상단 타이틀 ---
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "1:1 문의",
                style = Typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Large))

        // --- 이메일 및 휴대폰 정보 ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, // 양끝 배치
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("답변 받을 이메일", style = Typography.bodyMedium)
            Text("abc@def.com", style = Typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(Spacing.Small))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, // 양끝 배치
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("휴대폰 번호", style = Typography.bodyMedium)
            Text("010-1234-5678", style = Typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(Spacing.Medium))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(Spacing.Medium))

        // --- 안내 문구 ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text("이메일 및 휴대번호는 '내 정보 수정'에서 변경할 수 있습니다.", style = Typography.labelMedium, color = Color.Gray)
            Text("1:1 문의는 실시간 상담이 아니며, 답변까지 시일이 소요될 수 있습니다.", style = Typography.labelMedium, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(Spacing.Large))

        // --- 제목 입력 ---
        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            placeholder = { Text("제목") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(Spacing.Small))

        // --- 내용 입력 ---
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            placeholder = { Text("내용") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 남은 세로 공간을 모두 차지
        )

        Spacer(modifier = Modifier.height(Spacing.Medium))

        // --- 하단 버튼 ---
        Button(
            onClick = { /* TODO: 문의하기 로직 */ },
            modifier = Modifier.fillMaxWidth(),
            enabled = subject.isNotBlank() && content.isNotBlank()
        ) {
            Text("문의하기")
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TextButton(onClick = {
                navController.navigate("my_consultation")
            }) {
                Text("내 문의내역 확인하기 >")
            }
        }
    }
}