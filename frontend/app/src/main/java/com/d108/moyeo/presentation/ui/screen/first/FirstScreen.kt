package com.d108.moyeo.presentation.ui.screen.first

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.util.BiometricAuthManager

@Composable
fun FirstScreen(navController: NavHostController,
                viewModel: FirstViewModel = hiltViewModel()) {

    BackHandler {
        viewModel.onBackClicked()
    }

    val context = LocalContext.current

    // 생체 인증 관련
    val activity = context as FragmentActivity
    val biometricManager = remember { BiometricAuthManager(activity) }

    // 생체인증이 불가능한 기기 판별
    val canAuth = remember { biometricManager.canAuthenticate() }
    val uiState by viewModel.uiState.collectAsState()

    val isButtonEnabled = when(uiState.currentStep) {
        FirstStep.BIOMETRICS -> true
        FirstStep.PIN -> uiState.pin.length == 6
    }

    LaunchedEffect(uiState.currentStep) {
        if (uiState.currentStep == FirstStep.BIOMETRICS && biometricManager.canAuthenticate()) {
            if (biometricManager.canAuthenticate()) {
                biometricManager.authenticate(
                    title = "생체 정보로 인증해주세요",
                    negativeButtonText = "PIN으로 인증하기",
                    onSuccess = {
                        viewModel.onDebugLoginClick3()
                    },
                    onError = { errorCode, errString ->
                        when (errorCode) {
                            BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                            BiometricPrompt.ERROR_LOCKOUT,
                            BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                                viewModel.skipBiometrics()
                            }
                            else -> {
                                // Toast.makeText(context, "인증 오류: $errString", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onFailed = {
                        Toast.makeText(context, "인증에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(context, "생체 인식을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                viewModel.skipBiometrics()
            }

        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is FirstNavigationEvent.NavigateToHome -> {
                    // 홈 화면으로 이동하고, 이전 화면 스택을 모두 제거합니다.
                    navController.navigate("home") { // "home"은 실제 홈 화면의 라우트 이름으로 변경해주세요.
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
                is FirstNavigationEvent.NavigateBack -> {
                    activity.finish() // 현재 액티비티를 종료합니다.
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Padding.HorizontalMedium, vertical = Padding.VerticalMedium)
    ) {
        Box(modifier = Modifier
            .weight(1f)
            .padding(Spacing.Medium)) { // 스텝에 따라서 컴포저블이 보일 영역

            when (uiState.currentStep) {
                FirstStep.BIOMETRICS -> BiometricsContent(viewModel)
                FirstStep.PIN -> PinContent(viewModel)
            }
        }

        if (uiState.currentStep == FirstStep.BIOMETRICS) {
            Button(
                onClick = viewModel::skipBiometrics,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.Medium),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.Black
                ),
                enabled = isButtonEnabled
            ) {
                Text(text = "핀으로 인증하기")

            }
        }
    }


}