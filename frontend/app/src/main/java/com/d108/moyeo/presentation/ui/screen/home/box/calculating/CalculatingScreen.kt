package com.d108.moyeo.presentation.ui.screen.home.box.calculating

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
fun CalculatingScreen(navController: NavController,
                      viewModel: CalculatingViewModel = viewModel()) {

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
                is CalculatingNavEvent.NavigateBack -> {
                    navController.popBackStack()
                }
                is CalculatingNavEvent.ShowBiometricPrompt -> {
                    if (biometricManager.canAuthenticate()) {
                        biometricManager.authenticate(
                            title = "본인 인증",
                            negativeButtonText = "PIN으로 인증하기",
                            onSuccess = { viewModel.onBiometricsSucceeded() },
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Padding.HorizontalMedium, vertical = Padding.VerticalMedium)
    ) {
        IconButton(onClick = { viewModel.onBackClick() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "뒤로가기",
                modifier = Modifier.size(Spacing.ExtraLarge)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(Spacing.Medium)
        ) { // 스텝에 따라서 컴포저블이 보일 영역
            when (uiState.currentStep) {
                CalculatingStep.CHOOSE_CURRENCY -> ChooseCurrencyContent(
                    selectedCurrency = uiState.currency,
                    onCurrencySelect = viewModel::onCurrencySelected
                )
                CalculatingStep.HOW_TO_CALCULATE -> HowToCalculateContent(viewModel = viewModel)
                CalculatingStep.BIOMETRIC -> BiometricContent()
                CalculatingStep.PIN -> PinContent(viewModel = viewModel)
                CalculatingStep.FINISH -> FinishContent(viewModel = viewModel)
            }
        }

        val isButtonEnabled = when (uiState.currentStep) {
            CalculatingStep.CHOOSE_CURRENCY -> uiState.currency.isNotBlank()
            CalculatingStep.HOW_TO_CALCULATE -> true // 정산 버튼 확인
            CalculatingStep.BIOMETRIC -> true
            CalculatingStep.PIN -> uiState.pin.length == 6
            CalculatingStep.FINISH -> true
        }

        if (uiState.currentStep != CalculatingStep.BIOMETRIC) {
            Button(
                onClick = { viewModel.onNextClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                enabled = isButtonEnabled
            ) {
                val buttonText = when (uiState.currentStep) {
                    CalculatingStep.FINISH -> "확인"
                    CalculatingStep.HOW_TO_CALCULATE -> "다음"
                    CalculatingStep.PIN -> "인증하기"
                    else -> "다음"
                }
                Text(buttonText)
            }
        } else {
            Button(
                onClick = { viewModel.onNextClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text("PIN으로 인증하기")
            }
        }
    }

}