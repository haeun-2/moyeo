package com.d108.moyeo.presentation.ui.screen

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun FirstScreen(navController: NavHostController) {  // 로그아웃 or 앱 최초 설치 후 보이는 화면

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

            // 우리 앱에 대한 설명이 들어갈 공간 (비율: 1)
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
            Button(  // 회원 가입 버튼
                onClick = {
                    navController.navigate(AppScreen.SignUp.route)
                },  // 이제 이걸 완성하자.
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("회원가입 하기")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    navController.navigate(AppScreen.Login.route)
                }) {
                    Text("이미 회원이신가요?")
                }

                TextButton(onClick = {
                   navController.navigate(AppScreen.Home.route)
                }) {
                    Text("디버깅용 즉시 홈으로 이동")
                }

            }
        }
    }
}


@Composable
fun NameInputScreen() {
    // 이름 입력값을 저장할 상태 변수
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Padding.HorizontalMedium, vertical = Padding.VerticalLarge),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "이름을\n입력해주세요.",
                style = Typography.titleLarge,
                textAlign = TextAlign.Left
            )
        }

        Spacer(modifier = Modifier.height(Spacing.ExtraLarge))

        // --- 이름 입력 필드 ---
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("박보검") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // 이 Spacer가 버튼을 화면 하단으로 밀어냅니다.
        Spacer(modifier = Modifier.weight(1f))

        // --- 하단 '다음' 버튼 ---
        Button(
            onClick = { /* TODO: ViewModel의 onNextClicked() 호출 */ },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() // 이름이 입력되었을 때만 버튼 활성화
        ) {
            Text("다음")
        }
    }
}