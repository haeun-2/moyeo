package com.d108.moyeo.presentation.ui.screen.more.account.change

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.onSurfaceLight
import com.d108.moyeo.presentation.theme.primaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyAccountContent(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    verificationCode: String,
    isLoading: Boolean,
    error: String?,
    onCodeChange: (String) -> Unit,
    onConfirmClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("인증번호 입력") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = modifier
                .padding(inner)
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text("해당 계좌로\n1원을 보냈어요", style = Typography.titleLarge)
            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                OutlinedTextField(
                    value = verificationCode,
                    onValueChange = onCodeChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = {
                        Text(
                            text = "입금자 명(또는 인증코드)을 입력해주세요",
                            style = Typography.bodyMedium,
                            color = onSurfaceLight
                        )
                    },
                    shape = RoundedCornerShape(15.dp)
                )
                // 타이머는 추후 ViewModel에 로직 추가 시 교체
                Text(
                    text = "3:00",
                    modifier = Modifier.padding(end = 16.dp),
                    style = Typography.bodyMedium,
                    color = primaryLight
                )
            }

            if (!error.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(text = error, color = MaterialTheme.colorScheme.error, style = Typography.bodySmall)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onConfirmClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                }
                Text("변경하기")
            }
        }
    }
}
