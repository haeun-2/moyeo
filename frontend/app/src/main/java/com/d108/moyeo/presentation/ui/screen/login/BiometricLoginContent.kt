package com.d108.moyeo.presentation.ui.screen.login

import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.d108.moyeo.util.BiometricAuthManager

@Composable
fun BiometricLoginContent(viewModel: LoginViewModel) {
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val biometricManager = remember { BiometricAuthManager(activity) }

    // 이 Composable이 화면에 나타나면, 생체 인증을 자동으로 시도
    LaunchedEffect(Unit) {
        if (biometricManager.canAuthenticate()) {
            biometricManager.authenticate(
                title = "지문을 인증해주세요",
                negativeButtonText = "비밀번호 로그인",
                onSuccess = { viewModel.onLoginSuccess() },
                onError = { errorCode, errString ->
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        viewModel.switchToPasswordMode()
                    } else {
                        Toast.makeText(context, "인증 에러 발생", Toast.LENGTH_SHORT).show()
                    }
                },
                onFailed = {
                    Toast.makeText(context, "인증에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            // 기기에서 생체 인식을 사용할 수 없는 경우, 바로 비밀번호 모드로 전환
            viewModel.switchToPasswordMode()
        }
    }

    // 생체 인증 대기 중에 보여줄 UI
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // 이 Spacer가 이미지 영역을 화면의 1/3 위치로 밀어냅니다.
        Spacer(modifier = Modifier.weight(1f))

        // 이미지가 들어갈 영역
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(Color.LightGray.copy(alpha = 0.3f)), // 영역을 회색으로 칠함
            contentAlignment = Alignment.Center
        ) {
            Text("모여 로고 들어올 영역")
        }

        // 이 Spacer가 하단의 남은 공간을 모두 차지합니다.
        Spacer(modifier = Modifier.weight(2f))
    }
}