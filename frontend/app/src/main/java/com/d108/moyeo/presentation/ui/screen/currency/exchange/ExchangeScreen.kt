package com.d108.moyeo.presentation.ui.screen.currency.exchange

import android.widget.Toast
import androidx.activity.compose.BackHandler
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

    BackHandler { viewModel.onBackClick(); navController.popBackStack() }
    LaunchedEffect(Unit) { viewModel.initIfNeeded() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val boxes  by viewModel.boxUiStates.collectAsStateWithLifecycle(initialValue = emptyList())
    val currencies by viewModel.currencies.collectAsStateWithLifecycle(initialValue = emptyList())

    val isButtonEnabled = when (uiState.step) {
        ExchangeStep.TARGET_BOX -> uiState.selectedBoxId > 0L
        ExchangeStep.CHOOSE_CURRENCY -> uiState.spendCurrencyCode.isNotBlank()
        ExchangeStep.HOW_MUCH -> {
            val amountInput = uiState.amount.toDoubleOrNull() ?: 0.0
            amountInput > 0.0 && uiState.requiredSpendAmount <= uiState.availableSpendBalance
        }
        ExchangeStep.BIOMETRIC -> true
        ExchangeStep.PIN -> uiState.pin.length == 6
        ExchangeStep.FINISH -> true
    }

    val context = LocalContext.current
    val activity = context as FragmentActivity
    val biometricManager = remember { BiometricAuthManager(activity) }

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is ExchangeNavEvent.NavigateBack -> navController.popBackStack()
                is ExchangeNavEvent.ShowBiometricPrompt -> {
                    if (biometricManager.canAuthenticate()) {
                        biometricManager.authenticate(
                            title = "본인 인증",
                            negativeButtonText = "PIN으로 인증",
                            onSuccess = { viewModel.onBiometricsSucceeded() },
                            onError = { _, _ -> viewModel.skipBiometrics() },
                            onFailed = { viewModel.skipBiometrics() } // 실패 시에도 PIN으로
                        )
                    } else {
                        Toast.makeText(context, "생체 인식을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        viewModel.skipBiometrics()
                    }
                }
            }
        }
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
                            targetCurrencyName = uiState.targetCurrencyName,
                            selectedBoxId = uiState.selectedBoxId,
                            onBoxSelect = viewModel::onTargetBoxSelected,
                            boxes = boxes,
                            onNext = viewModel::onNextClicked
                        )
                    }
                    ExchangeStep.CHOOSE_CURRENCY -> {
                        // CHARGE에서만 보임 (지출 통화 선택)
                        ChooseCurrencyContent(
                            targetCurrencyName = uiState.targetCurrencyName,
                            paddingValues = PaddingValues(0.dp),
                            selected = uiState.spendCurrencyCode,
                            currencies = currencies,
                            onSelect = { code, _ -> viewModel.onCurrencySelected(code) },
                        )
                    }
                    ExchangeStep.HOW_MUCH -> {
                        HowMuchContent(
                            mode = uiState.mode,
                            chargingCurrencyName = uiState.targetCurrencyName,
                            chargingCurrencyCode = uiState.targetCurrencyCode,
                            spendingCurrencyName = uiState.spendCurrencyName,
                            spendingCurrencyCode = uiState.spendCurrencyCode,
                            amount = uiState.amount,
                            onKeyPress = viewModel::onKeyPress,
                            availableBalance = uiState.availableSpendBalance,
                            requiredSpendAmount = uiState.requiredSpendAmount,
                            isMultiStepExchange = uiState.isMultiStepExchange,
                            rateForStep1 = uiState.rateForStep1,
                            rateForStep2 = uiState.rateForStep2
                        )
                    }
                    ExchangeStep.BIOMETRIC -> BiometricContent()
                    ExchangeStep.PIN -> PinContent(
                        pin = uiState.pin,
                        pinError = uiState.pinError,
                        isPinLocked = uiState.isPinLocked,
                        onKeyPress = viewModel::onKeyPress
                    )
                    ExchangeStep.FINISH -> {
                        FinishContent(
                            mode = uiState.mode,
                            chargedAmount = uiState.amount,
                            chargedCurrency = uiState.targetCurrencyName,
                            spentAmount = uiState.requiredSpendAmount,
                            spentCurrency = uiState.spendCurrencyName,
                            onDone = { navController.popBackStack() }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = viewModel::onNextClicked,
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("다음")
            }
        }
    }
}