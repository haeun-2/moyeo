package com.d108.moyeo.presentation.ui.screen.signup

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.onSurfaceLight

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Padding.HorizontalMedium, vertical = Padding.VerticalMedium)
    ) {
        Box(modifier = Modifier.weight(1f).padding(Spacing.Medium)) { // 스텝에 따라서 컴포저블이 보일 영역
            when (uiState.currentStep) {
                SignUpStep.NAME -> NameInputContent(uiState, viewModel)
                SignUpStep.ACCOUNT -> AccountInputContent(uiState, viewModel)
                SignUpStep.VERIFY_ACCOUNT -> VerifyAccountContent(uiState, viewModel)
                SignUpStep.TERMS -> TermsContent(uiState, viewModel)
                SignUpStep.PIN -> PinInputContent(uiState, viewModel)
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
            SignUpStep.PIN -> uiState.pin.length == 6 && uiState.pin == uiState.pinConfirm
            SignUpStep.BIOMETRICS -> true
            SignUpStep.COMPLETE -> true
        }

        Button(
            onClick = viewModel::onNextClicked,
            modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.Medium),
            enabled = isButtonEnabled
        ) {
            val buttonText = when(uiState.currentStep) {
                SignUpStep.COMPLETE -> "시작하기"
                SignUpStep.BIOMETRICS -> "완료"
                else -> "다음"
            }
            Text(buttonText)
        }
    }
}

// --- 각 단계별 UI는 별도의 Composable 함수로 분리 ---
@Composable
private fun NameInputContent(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    Column {
        Text("이름을\n입력해주세요", style = Typography.titleLarge)
        Spacer(Modifier.height(20.dp))  // 후에 상수화 할 것
        OutlinedTextField(
            value = uiState.name,
            onValueChange = viewModel::onNameChanged,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(text = "홍길동",
                    style = Typography.bodyMedium,
                    color = onSurfaceLight
                )}, // 플레이스홀더 텍스트 설정
            shape = RoundedCornerShape(15.dp)  // 모서리를 둥글게 설정
        )
    }
}

@Composable
private fun AccountInputContent(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    Column {
        Text("계좌번호를 입력해주세요.", style = Typography.titleLarge)
        Spacer(Modifier.height(32.dp))
        TextField(
            value = uiState.accountBank,
            onValueChange = viewModel::onAccountBankChanged,
            modifier = Modifier.fillMaxWidth()) // TODO: 은행 선택 UI로 변경
        Spacer(Modifier.height(16.dp))
        TextField(
            value = uiState.accountNumber,
            onValueChange = viewModel::onAccountNumberChanged,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
    }
}

@Composable
private fun VerifyAccountContent(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    Column {
        Text("계좌로 1원을 보냈습니다.", style = Typography.titleLarge)
        Text("입금자명 뒤 숫자 4자리를 입력해주세요.", style = Typography.bodyMedium)
        Spacer(Modifier.height(32.dp))
        TextField(
            value = uiState.oneCoinNumber,
            onValueChange = viewModel::onOneCoinNumberChanged,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
    }
}

@Composable
private fun TermsContent(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    Column {
        Text("약관에 동의해주세요.", style = Typography.titleLarge)
        Spacer(Modifier.height(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                // 전체 동의 상태를 사용하고, 클릭 시 onAllTermsChanged 함수 호출
                checked = uiState.allTermsAccepted,
                onCheckedChange = { viewModel.onAllTermsChanged(!uiState.allTermsAccepted) }
            )
            Text("전체 동의")
        }
        HorizontalDivider()
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                // 서비스 이용약관 상태를 사용하고, 클릭 시 onTermsOfServiceChanged 함수 호출
                checked = uiState.termsOfServiceAccepted,
                onCheckedChange = { viewModel.onTermsOfServiceChanged(!uiState.termsOfServiceAccepted) }
            )
            Text("서비스 이용약관 (필수)", modifier = Modifier.weight(1f))
            TextButton(
                onClick = { /*TODO*/ }
            ) { Text("보기") }
        }
        // TODO: 개인정보 처리방침 등 다른 약관들도 위와 같은 방식으로 추가
    }
}

@Composable
private fun PinInputContent(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    Column {
        Text("사용하실 PIN 6자리를 입력해주세요.", style = Typography.titleLarge)
        Spacer(Modifier.height(32.dp))
        TextField(
            value = uiState.pin,
            onValueChange = viewModel::onPinChanged,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword), visualTransformation = PasswordVisualTransformation())
        Spacer(Modifier.height(16.dp))
        TextField(
            value = uiState.pinConfirm,
            onValueChange = viewModel::onPinConfirmChanged,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword), visualTransformation = PasswordVisualTransformation())
    }
}

@Composable
private fun BiometricsContent(viewModel: SignUpViewModel) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("생체 인증을 사용하시겠어요?", style = Typography.titleLarge, textAlign = TextAlign.Center)
        Text("더욱 빠르고 안전하게 로그인할 수 있습니다.", style = Typography.bodyMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))
        Row {
            OutlinedButton(onClick = viewModel::onNextClicked, modifier = Modifier.weight(1f)) { Text("건너뛰기") }
            Spacer(Modifier.width(16.dp))
            Button(onClick = viewModel::onNextClicked,
                modifier = Modifier.weight(1f)
            ) {
                Text("사용하기")
            }
        }
    }
}

@Composable
private fun CompleteContent() {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "완료", modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text("회원가입이 완료되었습니다!", style = Typography.titleLarge, textAlign = TextAlign.Center)
    }
}