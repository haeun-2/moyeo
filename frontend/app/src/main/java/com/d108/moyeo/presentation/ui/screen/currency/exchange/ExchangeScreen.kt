package com.d108.moyeo.presentation.ui.screen.currency.exchange

import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.util.BiometricAuthManager

@Composable
fun ExchangeScreen(
    navController: NavController,
    mode: String,
    viewModel: ExchangeViewModel = hiltViewModel()
) {
    val isRefund = mode.equals("refund", ignoreCase = true)
    val title = if (isRefund) "돌려받기" else "충전하기"

    val context = LocalContext.current
    val activity = context as FragmentActivity
    val biometricManager = remember { BiometricAuthManager(activity) }

    BackHandler { viewModel.onBackClick(); navController.popBackStack() }
    LaunchedEffect(Unit) {
        viewModel.initIfNeeded()
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is ExchangeViewModel.ExchangeNavEvent.ShowBiometricPrompt -> {
                    if (biometricManager.canAuthenticate()) {
                        biometricManager.authenticate(
                            title = "본인 인증",
                            negativeButtonText = "PIN으로 인증하기",
                            onSuccess = { viewModel.onBiometricsSucceeded() },
                            onError = { code, _ ->
                                when (code) {
                                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                                    BiometricPrompt.ERROR_LOCKOUT,
                                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> viewModel.skipBiometrics()
                                    else -> { /* 토스트는 필요 시 추가 */ }
                                }
                            },
                            onFailed = { /* 실패 안내 필요 시 토스트 */ }
                        )
                    } else {
                        viewModel.skipBiometrics()
                    }
                }
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val boxes  by viewModel.boxUiStates.collectAsStateWithLifecycle(initialValue = emptyList())
    val currencies by viewModel.currencies.collectAsStateWithLifecycle(initialValue = emptyList())

    val isButtonEnabled = when (uiState.step) {
        ExchangeStep.TARGET_BOX      -> uiState.selectedBoxId > 0L
        ExchangeStep.CHOOSE_CURRENCY -> uiState.spendCurrencyCode.isNotBlank()
        ExchangeStep.HOW_MUCH        -> (uiState.amount.toLongOrNull() ?: 0L) > 0L
        ExchangeStep.BIOMETRIC       -> true
        ExchangeStep.PIN             -> uiState.pin.length == 6
        ExchangeStep.FINISH          -> true
    }

    val buttonText = when (uiState.step) {
        ExchangeStep.FINISH     -> "확인"
        ExchangeStep.HOW_MUCH   -> "진행하기"
        ExchangeStep.BIOMETRIC  -> "PIN으로 인증하기"
        ExchangeStep.PIN        -> "인증하기"
        else                    -> "다음"
    }

    Scaffold(
        topBar = {
            Box(Modifier.fillMaxWidth().padding(Spacing.Medium)) {
                IconButton(
                    onClick = { viewModel.onBackClick(); navController.popBackStack() },
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "뒤로가기")
                }
                Text(text = title, modifier = Modifier.align(Alignment.Center))
            }
        }
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
                when (uiState.step) {
                    ExchangeStep.TARGET_BOX -> {
                        // CHARGE에서만 보임
                        TargetBoxContent(
                            selectedBoxId = uiState.selectedBoxId,
                            onBoxSelect = viewModel::onTargetBoxSelected,
                            boxes = boxes,
                            onNext = viewModel::onNextClicked
                        )
                    }
                    ExchangeStep.CHOOSE_CURRENCY -> {
                        // CHARGE에서만 보임 (지출 통화 선택)
                        ChooseCurrencyContent(
                            paddingValues = PaddingValues(0.dp),
                            selected = uiState.spendCurrencyCode,
                            currencies = currencies,
                            onSelect = { code, _ -> viewModel.onCurrencySelected(code) },
                        )
                    }
                    ExchangeStep.HOW_MUCH -> {
                        HowMuchContent(
                            title = if (uiState.mode == ExchangeMode.CHARGE) "지출 금액을\n입력해주세요" else "환불할 금액을\n입력해주세요",
                            currencyName = uiState.spendCurrencyName.ifBlank { uiState.spendCurrencyCode.ifBlank { "통화 선택 필요" } },
                            amount = uiState.amount,
                            onAmountChange = viewModel::changeAmount,
                            onSubmit = viewModel::onNextClicked
                        )
                    }
                    ExchangeStep.BIOMETRIC -> BiometricContent()
                    ExchangeStep.PIN -> PinContent(viewModel = viewModel)
                    ExchangeStep.FINISH -> FinishContent(
                        mode = uiState.mode,
                        amount = uiState.amount,
                        currencyUnit = uiState.spendCurrencyCode,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            val onPrimaryAction: () -> Unit =
                if (uiState.step == ExchangeStep.FINISH) {
                    { navController.popBackStack() }
                } else {
                    { viewModel.onNextClicked() }
                }

            Button(
                onClick = onPrimaryAction,
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(buttonText)
            }
        }
    }
}