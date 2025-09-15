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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun FirstScreen(navController: NavHostController,
                viewModel: FirstViewModel = hiltViewModel()) {
    // 로그아웃 or 앱 최초 설치 후 보이는 화면

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is FirstNavEvent.NavigateToHome -> {
                    // 홈으로 이동하라는 이벤트가 오면, 실제 내비게이션을 수행합니다.
                    navController.navigate(AppScreen.Home.route) {
                        // FirstScreen으로 다시 돌아올 수 없도록 백스택에서 제거합니다.
                        popUpTo(AppScreen.First.route) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

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

                TextButton(
                    onClick = viewModel::onDebugLoginClick3, // ViewModel 함수와 연결합니다
                    enabled = !uiState.isLoading // 로딩 중에는 버튼 비활성화
                ) {
                    Text("디버깅용 3번 로그인")
                }

                TextButton(
                    onClick = viewModel::onDebugLoginClick4, // ViewModel 함수와 연결합니다
                    enabled = !uiState.isLoading // 로딩 중에는 버튼 비활성화
                ) {
                    Text("디버깅용 4번 로그인")
                }

            }
        }
    }
}