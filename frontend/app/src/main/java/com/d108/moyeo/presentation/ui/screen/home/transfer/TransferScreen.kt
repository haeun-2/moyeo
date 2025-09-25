package com.d108.moyeo.presentation.ui.screen.home.transfer

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

// TODO: 이체 시 돈이 모자랄 때 이체 취소 로직 추가

@Composable
fun TransferScreen(
    navController: NavController,
    viewModel: TransferViewModel = hiltViewModel()
) {
    // 생체 인증에 필요
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val biometricManager = remember { BiometricAuthManager(activity) }

    BackHandler { viewModel.onBackClick() }

    // 네비게이션/인증 이벤트 처리
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is TransferNavEvent.NavigateBack -> navController.popBackStack()
                is TransferNavEvent.ShowBiometricPrompt -> {
                    if (biometricManager.canAuthenticate()) {
                        biometricManager.authenticate(
                            title = "본인 인증",
                            negativeButtonText = "PIN으로 인증하기",
                            onSuccess = { viewModel.onBiometricsSucceeded() },
                            onError = { errorCode, errString ->
                                when (errorCode) {
                                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                                    BiometricPrompt.ERROR_LOCKOUT,
                                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> viewModel.skipBiometrics()
                                    else -> Toast.makeText(context, "인증 오류: $errString", Toast.LENGTH_SHORT).show()
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

    // 상태 구독
    val uiState by viewModel.uiState.collectAsState()
    val boxes by viewModel.groupBoxesUi.collectAsState()
    val currencies by viewModel.currencies.collectAsState()

    // 진행 버튼 활성화 여부
    val isButtonEnabled = when (uiState.currentStep) {
        TransferStep.TARGET_BOX     -> uiState.targetBox != -1L
        TransferStep.CHOOSE_CURRENCY-> uiState.currency.isNotBlank()
        TransferStep.HOW_MUCH       -> uiState.howMuch.isNotBlank()
        TransferStep.BIOMETRIC      -> true
        TransferStep.PIN            -> uiState.pin.length == 6
        TransferStep.FINISH         -> true
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
                    end = Spacing.ExtraLarge,
                )
        ) {
            // 스텝별 콘텐츠 영역
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                when (uiState.currentStep) {
                    TransferStep.TARGET_BOX -> TargetBoxContent(
                        boxes = boxes,
                        selectedBoxId = uiState.targetBox,
                        onBoxSelect = viewModel::onTargetBoxSelected
                    )
                    TransferStep.CHOOSE_CURRENCY -> ChooseCurrencyContent(
                        selectedCurrency = uiState.currency,
                        onCurrencySelect = viewModel::onCurrencySelected,
                        currencies = currencies,
                        title = if (uiState.mode == TransferMode.DEPOSIT) "어떤 통화로 입금할까요?" else "어떤 통화로 이체할까요?"
                    )
                    TransferStep.HOW_MUCH   -> HowMuchContent(viewModel = viewModel)
                    TransferStep.BIOMETRIC  -> BiometricContent()
                    TransferStep.PIN        -> PinContent(viewModel = viewModel)
                    TransferStep.FINISH     -> FinishContent(viewModel = viewModel)
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
                    TransferStep.FINISH    -> "확인"
                    TransferStep.HOW_MUCH  -> "보내기"
                    TransferStep.BIOMETRIC -> "PIN으로 인증하기"
                    TransferStep.PIN       -> "인증하기"
                    else                   -> "다음"
                }
                Text(buttonText)
            }
        }
    }
}
