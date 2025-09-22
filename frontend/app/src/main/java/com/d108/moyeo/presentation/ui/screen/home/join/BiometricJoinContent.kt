package com.d108.moyeo.presentation.ui.screen.home.join

import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.d108.moyeo.util.BiometricAuthManager

@Composable
fun BiometricJoinContent(
    title: String = "지문을 인증해주세요",
    negativeButtonText: String = "PIN으로 인증",
    onSuccess: () -> Unit,
    onNegative: () -> Unit,          // 취소 버튼 → PIN 폴백
    onErrorOrFailed: () -> Unit       // 에러/실패 공통 처리
) {
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val biometricManager = remember { BiometricAuthManager(activity) }

    LaunchedEffect(Unit) {
        if (biometricManager.canAuthenticate()) {
            biometricManager.authenticate(
                title = title,
                negativeButtonText = negativeButtonText,
                onSuccess = { onSuccess() },
                onError = { code, _ ->
                    if (code == BiometricPrompt.ERROR_NEGATIVE_BUTTON) onNegative()
                    else onErrorOrFailed()
                },
                onFailed = { onErrorOrFailed() }
            )
        } else {
            onNegative() // 사용 불가 → PIN으로
        }
    }

    // 대기용 심플 플레이스홀더
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(Modifier.size(150.dp).background(Color.LightGray.copy(alpha = 0.3f)))
    }
}
