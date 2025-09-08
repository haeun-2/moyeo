package com.d108.moyeo.presentation.ui.screen.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.errorLight
import com.d108.moyeo.presentation.theme.primaryLight
import com.d108.moyeo.presentation.theme.secondaryLight

@Composable
fun ChangePasswordScreen(navController: NavController) {
    // 각 입력 필드와 상태를 Composable 내에서 직접 관리. 추후에 뷰모델 및 스테이트플로우로 관리해야 함.
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLogoutChecked by remember { mutableStateOf(false) }

    // 임시 현재 비밀번호
    val correctCurrentPassword = "111111"

    // 유효성 검증 상태
    val isCurrentPasswordCorrect = currentPassword == correctCurrentPassword
    val doNewPasswordsMatch = newPassword.isNotEmpty() && newPassword == confirmPassword
    val isNewPasswordValid = newPassword.length == 6

    // 최종 버튼 활성화 조건
    val isButtonEnabled = isCurrentPasswordCorrect && doNewPasswordsMatch && isNewPasswordValid && isLogoutChecked

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = Spacing.Medium, end = Spacing.Medium,
                top = Padding.ScreenTop, bottom = Padding.ScreenBottom
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 상단 타이틀 ---
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "비밀번호 변경",
                style = Typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.height(Spacing.Large))

        // --- 안내 문구 (왼쪽 정렬) ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "비밀번호를 입력해주세요!", style = Typography.titleLarge)
            Text(text = "6자리 숫자를 입력해주세요.", style = Typography.bodyMedium, color = secondaryLight)
        }

        // --- 현재 비밀번호 입력 ---
        Spacer(modifier = Modifier.height(Padding.HorizontalLarge))
        OutlinedTextField(
            value = currentPassword,
            onValueChange = {
                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                    currentPassword = it
                }
            },
            placeholder = { Text("사용 중인 비밀번호") }, // label을 placeholder로 변경
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            textStyle = Typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (currentPassword.isNotEmpty()) {
                    val text = if (isCurrentPasswordCorrect) "현재 비밀번호와 일치합니다." else "현재 비밀번호와 일치하지 않습니다."
                    val color = if (isCurrentPasswordCorrect) primaryLight else errorLight
                    Text(text, color = color)
                }
            }
        )

        // --- 구분선 ---
        Spacer(modifier = Modifier.height(Padding.VerticalLarge))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(Padding.VerticalLarge))

        // --- 새 비밀번호 입력 ---
        OutlinedTextField(
            value = newPassword,
            onValueChange = {
                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                    newPassword = it
                }
            },
            placeholder = { Text("새 비밀번호") }, // label을 placeholder로 변경
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            textStyle = Typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(Spacing.Medium))

        // --- 새 비밀번호 확인 입력 ---
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                    confirmPassword = it
                }
            },
            placeholder = { Text("새 비밀번호 확인") }, // 초기 힌트
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            textStyle = Typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                // 확인 비밀번호 필드에 입력이 시작되었을 때만 메시지를 표시하도록 수정
                if (confirmPassword.isNotEmpty()) {
                    if (doNewPasswordsMatch) {
                        Text("새 비밀번호가 일치합니다.", color = primaryLight)
                    } else {
                        Text("새 비밀번호가 일치하지 않습니다.", color = errorLight)
                    }
                }
            }
        )

        // --- 로그아웃 동의 ---
        Spacer(modifier = Modifier.weight(1f)) // 남은 공간을 모두 차지
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(
                checked = isLogoutChecked,
                onCheckedChange = { isLogoutChecked = it }
            )
            Text("모든 서비스에서 로그아웃됩니다.", style = Typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(Spacing.Small))

        // --- 변경 버튼 ---
        Button(
            onClick = { /* TODO: 비밀번호 변경 로직 구현 */ },
            modifier = Modifier.fillMaxWidth(),
            enabled = isButtonEnabled
        ) {
            Text("비밀번호 변경")
        }
    }
}

