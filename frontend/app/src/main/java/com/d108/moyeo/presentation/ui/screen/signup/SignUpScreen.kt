package com.d108.moyeo.presentation.ui.screen.signup

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.d108.moyeo.presentation.theme.primaryLight
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

        Button(
            onClick = viewModel::onNextClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.Medium),
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
            shape = RoundedCornerShape(15.dp)  // 모서리를 둥글게 설정. 후에 상수화 할 것
        )
    }
}

@Composable
private fun AccountInputContent(
    uiState: SignUpUiState,
    viewModel: SignUpViewModel,
    onBankFieldClick: () -> Unit) {
        Column {
            Text("연결할 계좌번호를\n입력해주세요", style = Typography.titleLarge)

            Spacer(Modifier.height(20.dp))  // 후에 상수화 할 것

            Box(
                modifier = Modifier.clickable(onClick = onBankFieldClick) // 클릭 이벤트를 Box로 옮겼습니다.
            ) {
                OutlinedTextField(
                    value = uiState.accountBank,
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false,  // 포커스 및 커서 깜빡임을 방지
                    singleLine = true,
                    placeholder = {
                        Text(text = "은행을 선택해주세요")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = onSurfaceLight,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    shape = RoundedCornerShape(15.dp)  // 모서리를 둥글게 설정. 후에 상수화 할 것
                )
            }


            Spacer(Modifier.height(20.dp))  // 후에 상수화 할 것

            OutlinedTextField(
                value = uiState.accountNumber,
                onValueChange = viewModel::onAccountNumberChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = {
                    Text(text = "계좌번호를 입력해주세요",
                        style = Typography.bodyMedium,
                        color = onSurfaceLight
                    )},// 플레이스홀더 텍스트 설정
                shape = RoundedCornerShape(15.dp)  // 모서리를 둥글게 설정. 후에 상수화 할 것
            )
        }
}

@Composable
private fun VerifyAccountContent(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    Column {
        Text("해당 계좌로\n1원을 보냈어요", style = Typography.titleLarge)

        Spacer(Modifier.height(20.dp))  // 후에 상수화 할 것

        Box(modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd) {// 오른쪽 끝, 세로 중앙에 정렬

            OutlinedTextField(
                value = uiState.oneCoinNumber,
                onValueChange = viewModel::onOneCoinNumberChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "입금자 명을 입력해주세요",
                        style = Typography.bodyMedium,
                        color = onSurfaceLight
                    )
                }, // 플레이스홀더 텍스트 설정
                shape = RoundedCornerShape(15.dp)  // 모서리를 둥글게 설정. 후에 상수화 할 것
            )
            Text(
                text = "3:00",
                modifier = Modifier.padding(end = 16.dp), // TextField의 테두리와 겹치지 않도록 패딩 추가
                style = Typography.bodyMedium,
                color = primaryLight // 타이머 색상을 강조색으로 설정
            )
        }
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
    PinContentLayout(
        title = "사용하실 PIN 6자리를 입력해주세요.",
        pinValue = uiState.pin,
        onDigitClick = { digit -> viewModel.onPinInput(digit, isConfirm = false) },
        onClearClick = { viewModel.onPinClear(isConfirm = false) },
        onBackspaceClick = { viewModel.onPinBackspace(isConfirm = false) }
    )
}

@Composable
private fun PinConfirmContent(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    PinContentLayout(
        title = "PIN을 한번 더 입력해주세요.",
        pinValue = uiState.pinConfirm,
        onDigitClick = { digit -> viewModel.onPinInput(digit, isConfirm = true) },
        onClearClick = { viewModel.onPinClear(isConfirm = true) },
        onBackspaceClick = { viewModel.onPinBackspace(isConfirm = true) },
        errorMessage = if (uiState.pinConfirm.length == 6 && uiState.pin != uiState.pinConfirm) "PIN이 일치하지 않습니다." else null
    )
}

// PIN 입력/확인 화면의 공통 레이아웃
@Composable
private fun PinContentLayout(
    title: String,
    pinValue: String,
    onDigitClick: (String) -> Unit,
    onClearClick: () -> Unit,
    onBackspaceClick: () -> Unit,
    errorMessage: String? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(title, style = Typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))

        // 네모칸 6개
        PinDisplay(pinLength = pinValue.length)

        if (errorMessage != null) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error, style = Typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.weight(1f))

        // 커스텀 키패드
        PinKeypad(
            onDigitClick = onDigitClick,
            onClearClick = onClearClick,
            onBackspaceClick = onBackspaceClick
        )
    }
}

// PIN 입력 상태를 보여주는 6개의 점
@Composable
private fun PinDisplay(pinLength: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(6) { index ->
            val isFilled = index < pinLength
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = if (isFilled) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            )
        }
    }
}

// 4x3 커스텀 키패드
@Composable
private fun PinKeypad(
    onDigitClick: (String) -> Unit,
    onClearClick: () -> Unit,
    onBackspaceClick: () -> Unit
) {
    val buttons = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "초기화", "0", "←"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        items(buttons) { key ->
            TextButton(
                onClick = {
                    when (key) {
                        "초기화" -> onClearClick()
                        "←" -> onBackspaceClick()
                        else -> onDigitClick(key)
                    }
                },
                modifier = Modifier.aspectRatio(1.5f),
                shape = CircleShape,
                border = if (key == "초기화" || key == "←") null else BorderStroke(1.dp, Color.LightGray)
            ) {
                Text(key, style = Typography.headlineMedium)
            }
        }
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