package com.d108.moyeo.presentation.ui.screen.signup

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.ui.component.signup.BankSelectionBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = viewModel()
) {

    BackHandler {
        viewModel.onBackClicked()
    }

    val uiState by viewModel.uiState.collectAsState()

    var showBankBottomSheet by remember { mutableStateOf(false) }
    if (showBankBottomSheet) {
        BankSelectionBottomSheet(
            banks = viewModel.bankList,
            onBankSelected = { selectedBank ->
                viewModel.onAccountBankChanged(selectedBank) // ViewModel에 선택된 은행 전달
                showBankBottomSheet = false // 바텀시트 닫기
            },
            onDismiss = {
                showBankBottomSheet = false // 바텀시트 닫기
            }
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
                SignUpStep.ACCOUNT -> AccountInputContent(uiState, viewModel, onBankFieldClick = { showBankBottomSheet = true })
                SignUpStep.VERIFY_ACCOUNT -> VerifyAccountContent(uiState, viewModel)
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
            SignUpStep.ACCOUNT -> uiState.accountBank.isNotBlank() && uiState.accountNumber.isNotBlank()
            SignUpStep.VERIFY_ACCOUNT -> true // TODO: 인증번호 유효성 검사
            SignUpStep.TERMS -> uiState.termsOfServiceAccepted && uiState.privacyPolicyAccepted
            SignUpStep.PIN -> uiState.pin.length == 6
            SignUpStep.PIN_CONFIRM -> uiState.pinConfirm.length == 6 && uiState.pin == uiState.pinConfirm
            SignUpStep.BIOMETRICS -> true
            SignUpStep.COMPLETE -> true
        }

        // 생체 인증 단계에서만 보일 화면
        if (uiState.currentStep == SignUpStep.BIOMETRICS) {
            Button(
                onClick = viewModel::onNextClicked, // "완료" 버튼과 동일한 동작. 추후 수정 필요.
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.Medium)
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