package com.d108.moyeo.presentation.ui.screen.home.charge

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.util.BiometricAuthManager

// TODO: 충전 시 돈이 모자랄 때 충전 취소 로직 추가

@Composable
fun ChargeScreen(
    navController: NavController,
    viewModel: ChargeViewModel = hiltViewModel()
) {

    // 생체 인증에 필요
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val biometricManager = remember { BiometricAuthManager(activity) }

    BackHandler {
        viewModel.onBackClick()
    }

    // ViewModel의 내비게이션 이벤트를 구독하고 처리
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is ChargeNavEvent.NavigateBack -> {
                    navController.popBackStack()
                }

                is ChargeNavEvent.ShowBiometricPrompt -> {
                    if (biometricManager.canAuthenticate()) {
                        biometricManager.authenticate(
                            title = "본인 인증",
                            negativeButtonText = "PIN으로 인증하기",
                            onSuccess = {
                                viewModel.onBiometricsSucceeded()
                            },
                            onError = { errorCode, errString ->
                                when (errorCode) {
                                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                                    BiometricPrompt.ERROR_LOCKOUT,
                                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                                        viewModel.skipBiometrics()
                                    }
                                    else -> {
                                        Toast.makeText(context, "인증 오류: $errString", Toast.LENGTH_SHORT).show()
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
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    val isButtonEnabled = when (uiState.currentStep) {
        ChargeStep.HOW_MUCH -> uiState.howMuch.isNotBlank() && uiState.howMuch != "0"
        ChargeStep.BIOMETRIC -> true
        ChargeStep.PIN -> uiState.pin.length == 6
        ChargeStep.FINISH -> true
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.Medium)
            ) {
                IconButton(
                    onClick = { viewModel.onBackClick() },
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "뒤로가기"
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = Spacing.ExtraLarge,
                    end = Spacing.ExtraLarge
                )
        ) {
            Box(modifier = Modifier.weight(1f)) {
                when (uiState.currentStep) {
                    ChargeStep.HOW_MUCH   -> HowMuchContent(viewModel = viewModel)
                    ChargeStep.BIOMETRIC  -> BiometricContent()
                    ChargeStep.PIN        -> PinContent(viewModel = viewModel)
                    ChargeStep.FINISH     -> FinishContent(viewModel = viewModel)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 하단 진행 버튼
            Button(
                onClick = { viewModel.onNextClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(bottom = Spacing.Medium),
                enabled = isButtonEnabled
            ) {
                val buttonText = when (uiState.currentStep) {
                    ChargeStep.FINISH    -> "확인"
                    ChargeStep.HOW_MUCH  -> "충전하기"
                    ChargeStep.BIOMETRIC -> "PIN으로 인증하기"
                    ChargeStep.PIN       -> "인증하기"
                }
                Text(buttonText)
            }
        }
    }
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(horizontal = Padding.HorizontalMedium, vertical = Padding.VerticalMedium)
//    ) {
//        IconButton(onClick = { viewModel.onBackClick() }) {
//            Icon(
//                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
//                contentDescription = "뒤로가기",
//                modifier = Modifier.size(32.dp)
//            )
//        }
//
//        Box(
//            modifier = Modifier  // 스텝에 따라서 컴포저블이 보일 영역
//                .weight(1f)
//                .padding(Spacing.Medium)
//        ) {
//            when (uiState.currentStep) {
//                ChargeStep.HOW_MUCH -> HowMuchContent(viewModel = viewModel)
//                ChargeStep.BIOMETRIC -> BiometricContent()
//                ChargeStep.PIN -> PinContent(viewModel = viewModel)
//                ChargeStep.FINISH -> FinishContent(viewModel = viewModel)
//            }
//        }
//
//        Button(
//            onClick = { viewModel.onNextClicked() },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(52.dp)
//                .padding(bottom = Spacing.Medium),
//            enabled = isButtonEnabled
//        ) {
//            val buttonText = when (uiState.currentStep) {
//                ChargeStep.FINISH -> "확인"
//                ChargeStep.HOW_MUCH -> "충전하기"
//                ChargeStep.BIOMETRIC -> "PIN으로 인증하기"
//                ChargeStep.PIN -> "인증하기"
//            }
//            Text(buttonText)
//        }
//    }
}
