package com.d108.moyeo.presentation.ui.screen.more.account.change

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.domain.model.Bank
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.ui.component.signup.BankSelectionBottomSheet

// TODO: 변경 완료 시 Toast 출력

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountChangeScreen(
    navController: NavController,
    viewModel: AccountChangeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 은행 목록(프리패치 + 시트 열릴 때 보장 호출)
    val banks by viewModel.banks.collectAsState(initial = emptyList())
    val isBanksLoading by viewModel.isBanksLoading.collectAsState()
    val banksError by viewModel.banksError.collectAsState()

    var showBankSheet by remember { mutableStateOf(false) }

    // DONE 단계가 되면 언제나 ConnectedAccountSettings 로 이동
    LaunchedEffect(uiState.step) {
        if (uiState.step == AccountChangeStep.DONE) {
            val targetRoute = AppScreen.ConnectedAccountSettings.route
            val popped = navController.popBackStack(targetRoute, inclusive = false)
            if (!popped) {
                navController.navigate(targetRoute) {
                    launchSingleTop = true
                }
            }
        }
    }

    // 뒤로가기: VERIFY 단계면 INPUT으로 되돌리고, 아니면 팝백
    BackHandler(enabled = true) {
        if (uiState.step == AccountChangeStep.VERIFY) {
            viewModel.backToInputStep()
        } else {
            navController.popBackStack()
        }
    }

    when (uiState.step) {
        AccountChangeStep.INPUT -> {
            AccountInputContent(
                modifier = Modifier.fillMaxSize(),
                paddingValues = PaddingValues(),
                selectedBankName = uiState.bankName,
                bankAccount = uiState.bankAccount,
                isLoading = uiState.isLoading,
                error = uiState.error,
                onSelectBankClick = {
                    viewModel.ensureBanksLoadedForSheet()
                    showBankSheet = true
                },
                onAccountChange = viewModel::updateBankAccount,
                onNextClick = {
                    viewModel.submitAccountInput(onNext = { /* 단일 스크린: 네비 불필요 */ })
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        AccountChangeStep.VERIFY -> {
            VerifyAccountContent(
                modifier = Modifier.fillMaxSize(),
                paddingValues = PaddingValues(),
                verificationCode = uiState.verificationCode,
                isLoading = uiState.isLoading,
                error = uiState.error,
                onCodeChange = viewModel::updateVerificationCode,
                onConfirmClick = {
                    viewModel.submitVerification {}
                },
                onBackClick = { viewModel.backToInputStep() }
            )
        }

        AccountChangeStep.DONE -> {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }
    }

    // 은행 선택 바텀시트
    if (showBankSheet) {
        when {
            isBanksLoading -> {
                // 간단 로딩 시트 (원하시면 커스텀 UI로 대체)
                ModalBottomSheet(onDismissRequest = { showBankSheet = false }) {
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                }
            }
            !banksError.isNullOrBlank() -> {
                ModalBottomSheet(onDismissRequest = { showBankSheet = false }) {
                    Text(
                        text = banksError ?: "은행 목록을 불러오지 못했습니다.",
                        modifier = Modifier.fillMaxSize()
                    )
                    TextButton(onClick = { viewModel.ensureBanksLoadedForSheet() }) { Text("다시 시도") }
                }
            }
            else -> {
                BankSelectionBottomSheet(
                    banks = banks,
                    onBankSelected = { bank: Bank ->
                        viewModel.onBankSelected(bank) // code/name 동시 반영
                        showBankSheet = false
                    },
                    onDismiss = { showBankSheet = false }
                )
            }
        }
    }
}
