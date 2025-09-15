package com.d108.moyeo.presentation.ui.screen.signup

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.ui.component.signup.BankSelectionBottomSheet
import com.d108.moyeo.util.BiometricAuthManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel()
) {

    BackHandler {
        viewModel.onBackClicked()
    }

    val context = LocalContext.current
    val activity = context as FragmentActivity
    val biometricManager = remember { BiometricAuthManager(activity) }

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is SignUpNavigationEvent.NavigateToHome -> {
                    // 홈 화면으로 이동하라는 이벤트
                    // FirstScreen까지의 모든 화면을 스택에서 제거하고 홈으로 이동
                    navController.navigate(AppScreen.Home.route) {
                        popUpTo(AppScreen.First.route) {
                            inclusive = true
                        }
                    }
                }
                is SignUpNavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }

                is SignUpNavigationEvent.ShowBiometricPrompt -> {
                    if (biometricManager.canAuthenticate()) {
                        biometricManager.authenticate(
                            title = "지문 인증",
                            negativeButtonText = "건너뛰기",
                            onSuccess = {
                                Toast.makeText(context, "성공!", Toast.LENGTH_SHORT).show()
                                viewModel.onBiometricsSucceeded()
                            },
                            onError = { errorCode, errString ->
                                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                                    viewModel.skipBiometrics() // 건너뛰기 로직 호출
                                } else {
                                    // 그 외 다른 에러들은 토스트 메시지를 보여줍니다.
                                    Toast.makeText(context, "실패: $errString", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onFailed = {
                                Toast.makeText(context, "실패!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "생체 인식을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showBankBottomSheet) {
        BankSelectionBottomSheet(
            banks = uiState.bankList, // ViewModel이 제공하는 Bank 모델 리스트
            onBankSelected = viewModel::onBankSelected, // ViewModel의 함수를 직접 연결
            onDismiss = viewModel::onBankBottomSheetDismiss // ViewModel의 함수를 직접 연결
        )
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
                SignUpStep.NAME -> NameInputContent(uiState, viewModel)
                SignUpStep.EMAIL_INPUT -> EmailInputContent(uiState, viewModel)
                SignUpStep.EMAIL_VERIFY -> EmailVerifyContent(uiState, viewModel)
                SignUpStep.PHONE_INPUT -> PhoneInputContent(uiState, viewModel)
                SignUpStep.PHONE_VERIFY -> PhoneVerifyContent(uiState, viewModel)
                SignUpStep.ACCOUNT -> AccountInputContent(uiState, viewModel,
                        onBankFieldClick = viewModel::onBankFieldClicked)
                SignUpStep.ACCOUNT_VERIFY -> VerifyAccountContent(uiState, viewModel)
                SignUpStep.TERMS -> TermsContent(uiState, viewModel)
                SignUpStep.PIN -> PinInputContent(uiState, viewModel)
                SignUpStep.PIN_CONFIRM -> PinConfirmContent(uiState, viewModel)
                SignUpStep.BIOMETRICS -> BiometricsContent(viewModel)
                SignUpStep.COMPLETE -> CompleteContent()

            }
        }

        // 하단 버튼
        val isButtonEnabled = when(uiState.currentStep) {  // 각 버튼이 활성화되는 타이밍
            SignUpStep.NAME -> uiState.name.isNotBlank()
            SignUpStep.EMAIL_INPUT -> uiState.email.isNotBlank()
            SignUpStep.EMAIL_VERIFY -> uiState.emailCode.length == 6
            SignUpStep.PHONE_INPUT -> uiState.phoneNumber.isNotBlank()
            SignUpStep.PHONE_VERIFY ->  uiState.phoneCode.length == 6

            SignUpStep.ACCOUNT -> uiState.accountBank != null && uiState.accountNumber.isNotBlank()
            SignUpStep.ACCOUNT_VERIFY -> uiState.oneCoinNumber.length == 4

            SignUpStep.TERMS -> uiState.termsOfServiceAccepted && uiState.privacyPolicyAccepted
            SignUpStep.PIN -> uiState.pin.length == 6
            SignUpStep.PIN_CONFIRM -> uiState.pinConfirm.length == 6 && uiState.pin == uiState.pinConfirm
            SignUpStep.BIOMETRICS -> true
            SignUpStep.COMPLETE -> true

        }

        // 생체 인증 단계에서만 보일 건너뛰기 버튼
        if (uiState.currentStep == SignUpStep.BIOMETRICS) {
            Button(
                onClick = viewModel::skipBiometrics,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.Medium),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.Black
                )
            ) {
                Text("건너뛰기")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = viewModel::onNextClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.Medium),
            enabled = isButtonEnabled
        ) {
            val buttonText = when(uiState.currentStep) {
                SignUpStep.COMPLETE -> "시작하기"
                SignUpStep.BIOMETRICS -> "사용하기"
                else -> "다음"
            }
            Text(buttonText)
        }
    }
}