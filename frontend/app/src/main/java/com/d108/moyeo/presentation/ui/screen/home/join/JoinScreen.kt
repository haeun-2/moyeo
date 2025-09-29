package com.d108.moyeo.presentation.ui.screen.home.join

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinScreen(
    viewModel: JoinViewModel = hiltViewModel(),
    codeFromDeepLink: String? = null,
    onFinished: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 시작: PIN(또는 생체)부터
    LaunchedEffect(Unit) {
        viewModel.startAuthFlow(codeFromDeepLink?.takeIf { it.isNotBlank() })
    }

    // 오류는 Toast
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    // ViewModel이 PIN 입력 요청 → PIN 페이지로 전환 (별도 화면)
    var showPinPage by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.pinRequestEvent.collectLatest { showPinPage = true }
    }

    // PIN 페이지에서 뒤로가기 → 인증 취소
    if (showPinPage) {
        BackHandler {
            showPinPage = false
            viewModel.submitPinResult(false)
        }
    }

    Scaffold { innerPadding ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentStep) {
                JoinStep.PIN -> {
                    // 1) 생체 사용이면 지문부터 시도
                    if (uiState.biometricsEnabled && !showPinPage) {
                        BiometricJoinContent(
                            onSuccess = {
                                // 지문 성공 → BOX 단계
                                viewModel.onBiometricSuccess()
                            },
                            onNegative = {
                                // 사용자가 PIN 선택
                                showPinPage = true
                            },
                            onErrorOrFailed = {
                                // 지문 실패 → PIN 폴백
                                showPinPage = true
                            }
                        ) // 지문 대기 플레이스홀더 UI
                    }

                    // 2) 생체 미사용 or 폴백 → PIN “새 페이지”
                    if (!uiState.biometricsEnabled || showPinPage) {
                        var pinDigits by remember { mutableStateOf("") }
                        var pinError by remember { mutableStateOf<String?>(null) }

                        PinJoinContent(
                            pinLength = pinDigits.length,
                            errorMessage = pinError,
                            onDigit = { d ->
                                if (pinDigits.length < 6) {
                                    pinDigits += d.toString()
                                    if (pinDigits.length == 6) {
                                        // TODO: 실제 저장 PIN 비교로 교체
                                        val ok = true
                                        if (ok) {
                                            showPinPage = false
                                            viewModel.submitPinResult(true) // VM이 BOX로 전환
                                        } else {
                                            pinError = "PIN 번호가 일치하지 않습니다."
                                            pinDigits = ""
                                        }
                                    }
                                }
                            },
                            onBackspace = { if (pinDigits.isNotEmpty()) pinDigits = pinDigits.dropLast(1) },
                            onClear = { pinDigits = ""; pinError = null }
                        )
                    }
                }

                JoinStep.BOX -> {
                    JoinBoxContent(
                        uiState = uiState,
                        onJoinCodeChange = viewModel::onJoinCodeChanged,
                        onJoinClick = { viewModel.confirmJoin() }
                    )
                    if (uiState.isLoading) {
                        Box(Modifier.fillMaxSize().padding(innerPadding)) {
                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                        }
                    }
                }

                JoinStep.FINISHED -> JoinFinishedContent(onConfirmClick = onFinished)
            }
        }
    }
}
