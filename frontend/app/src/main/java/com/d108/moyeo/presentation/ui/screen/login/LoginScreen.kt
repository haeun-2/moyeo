package com.d108.moyeo.presentation.ui.screen.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginMode by viewModel.loginMode.collectAsState()

    // 로그인 성공 시 홈으로 이동하는 이벤트 처리
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is LoginNavigationEvent.NavigateToHome -> {
                    navController.navigate(AppScreen.Home.route) {
                        // 로그인 화면까지의 모든 백스택을 제거
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = Padding.HorizontalMedium, vertical = Padding.VerticalMedium)
    ) {
        Box(modifier = Modifier
            .weight(1f)
            .padding(Spacing.Medium)) {
            // ViewModel의 상태에 따라 다른 UI를 보여줌
            when (loginMode) {
                LoginMode.BIOMETRIC -> BiometricLoginContent(viewModel)
                LoginMode.PASSWORD -> PinLoginContent(viewModel)
            }
        }

    }
}

