package com.d108.moyeo.presentation.ui.screen.home.sending

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.util.BiometricAuthManager

@Composable
fun SendingScreen(navController: NavController,
                  viewModel: SendingViewModel = viewModel()) {

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
                is SendingNavEvent.NavigateBack -> {
                    // NavigateBack 이벤트를 받으면 실제 뒤로가기 동작 수행
                    navController.popBackStack()
                }

                is SendingNavEvent.ShowBiometricPrompt -> {
                    // 생체 인증을 사용할 수 있는지 먼저 확인
                    if (biometricManager.canAuthenticate()) {
                        // ViewModel로부터 생체 인증 창을 띄우라는 이벤트를 받으면 인증 절차 시작
                        biometricManager.authenticate(
                            title = "본인 인증",
                            negativeButtonText = "PIN으로 인증하기",
                            onSuccess = {
                                // 인증 성공 시, ViewModel에 성공했음을 알림
                                viewModel.onBiometricsSucceeded()
                            },
                            onError = { errorCode, errString ->
                                // 사용자가 'PIN으로 인증하기' 버튼을 눌렀을 때 (취소했을 때)
                                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                                    viewModel.skipBiometrics() // ViewModel에 건너뛰었음을 알림
                                } else {
                                    // 그 외 다른 에러들은 토스트 메시지를 보여줍니다.
                                    Toast.makeText(context, "인증 오류: $errString", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onFailed = {
                                // 인증 실패 시 (예: 지문 불일치)
                                Toast.makeText(context, "인증에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        // 기기에서 생체 인식을 사용할 수 없는 경우
                        Toast.makeText(context, "생체 인식을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        // 이 경우 바로 PIN 인증으로 넘어가도록 처리
                        viewModel.skipBiometrics()
                    }
                }
            }
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Padding.HorizontalMedium, vertical = Padding.VerticalMedium)
    ) {
        IconButton(onClick = { viewModel.onBackClick() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "뒤로가기",
                modifier = Modifier.size(32.dp)
            )
        }

        Box(modifier = Modifier  // 스텝에 따라서 컴포저블이 보일 영역
            .weight(1f)
            .padding(Spacing.Medium)) {
            when (uiState.currentStep) {
                SendingStep.CHOOSE_CURRENCY -> ChooseCurrencyContent(
                    selectedCurrency = uiState.currency,
                    onCurrencySelect = viewModel::onCurrencySelected
                )
                SendingStep.TARGET_BOX -> TargetBoxContent() // TODO: 구현 필요
                SendingStep.HOW_MUCH -> HowMuchContent() // TODO: 구현 필요
                SendingStep.BIOMETRIC -> BiometricContent() // TODO: 구현 필요
                SendingStep.PIN -> PinContent() // TODO: 구현 필요
                SendingStep.FINISH -> FinishContent() // TODO: 구현 필요
            }

        }

        val isButtonEnabled = when(uiState.currentStep) {
            SendingStep.CHOOSE_CURRENCY -> uiState.currency.isNotBlank()
            SendingStep.TARGET_BOX -> uiState.targetBox.isNotBlank()
            SendingStep.HOW_MUCH -> uiState.howMuch.isNotBlank()
            SendingStep.BIOMETRIC -> true // 이 단계는 자동 진행되므로 버튼 비활성화도 가능
            SendingStep.PIN -> uiState.pin.length == 6 // 6자리로 완료
            SendingStep.FINISH -> true
        }

        Button(
            onClick = { viewModel.onNextClicked() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            enabled = isButtonEnabled
        ) {
            // 2. 버튼 텍스트 로직 수정 (SendingStep에 맞게 수정)
            val buttonText = when(uiState.currentStep) {
                SendingStep.FINISH -> "확인"
                SendingStep.HOW_MUCH -> "보내기"
                SendingStep.PIN -> "인증하기"
                else -> "다음"
            }
            Text(buttonText)
        }
    }
}

