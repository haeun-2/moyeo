package com.d108.moyeo.presentation.ui.screen.more.account.change

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.domain.model.Bank
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.ui.component.signup.BankSelectionBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountChangeScreen(
    navController: NavController,
    viewModel: AccountChangeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 은행 목록(프리패치 + 시트 열릴 때 보장 호출)
    val banks by viewModel.banks.collectAsState(initial = emptyList())
    val isBanksLoading by viewModel.isBanksLoading.collectAsState()
    val banksError by viewModel.banksError.collectAsState()

    var showBankSheet by remember { mutableStateOf(false) }

    // DONE 단계가 되면 언제나 ConnectedAccountSettings 로 이동
    LaunchedEffect(uiState.step) {
        if (uiState.step == AccountChangeStep.DONE) {
            Toast.makeText(context, "계좌번호 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            val targetRoute = AppScreen.ConnectedAccountSettings.route
            // 1) 스택에 이미 있으면: SavedStateHandle 로 리프레시 신호 전달
            runCatching { navController.getBackStackEntry(targetRoute) }
                .onSuccess { entry ->
                    entry.savedStateHandle["refresh"] = true
                }

            // 2) 해당 목적지로 복귀 또는 이동
            val popped = navController.popBackStack(targetRoute, inclusive = false)
            if (!popped) {
                // 없으면 파라미터로 리프레시 신호를 전달 (선택 사항)
                navController.navigate("$targetRoute?refresh=true") {
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

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.Medium)
            ) {
                IconButton(
                    onClick = {
                        if (uiState.step == AccountChangeStep.VERIFY) viewModel.backToInputStep()
                        else navController.popBackStack()
                    },
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "뒤로가기"
                    )
                }
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
            // 스텝별 콘텐츠
            Box(modifier = Modifier.weight(1f)) {
                when (uiState.step) {
                    AccountChangeStep.INPUT -> {
                        AccountInputContent(
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
                                viewModel.submitAccountInput(onNext = { /* same screen */ })
                            }
                        )
                    }

                    AccountChangeStep.VERIFY -> {
                        VerifyAccountContent(
                            verificationCode = uiState.verificationCode,
                            isLoading = uiState.isLoading,
                            error = uiState.error,
                            onCodeChange = viewModel::updateVerificationCode,
                            onConfirmClick = { viewModel.submitVerification { } }
                        )
                    }

                    AccountChangeStep.DONE -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }

        // 은행 선택 바텀시트
        if (showBankSheet) {
            when {
                isBanksLoading -> {
                    ModalBottomSheet(onDismissRequest = { showBankSheet = false }) {
                        Box(Modifier.fillMaxWidth().padding(Spacing.Large), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }

                !banksError.isNullOrBlank() -> {
                    ModalBottomSheet(onDismissRequest = { showBankSheet = false }) {
                        Column(Modifier.fillMaxWidth().padding(Spacing.Large)) {
                            Text(text = banksError ?: "은행 목록을 불러오지 못했습니다.")
                            Spacer(Modifier.height(Spacing.Medium))
                            TextButton(onClick = { viewModel.ensureBanksLoadedForSheet() }) { Text("다시 시도") }
                        }
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
}
