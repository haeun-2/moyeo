package com.d108.moyeo.presentation.ui.screen.more.account.change

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.onSurfaceLight

@Composable
fun AccountInputContent(
    selectedBankName: String,
    bankAccount: String,
    isLoading: Boolean,
    error: String?,
    onSelectBankClick: () -> Unit,
    onAccountChange: (String) -> Unit,
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text("연결할 계좌번호를\n입력해주세요", style = Typography.titleLarge)
        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .clickable(onClick = onSelectBankClick)
        ) {
            OutlinedTextField(
                value = selectedBankName.ifBlank { "" },
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false,
                singleLine = true,
                placeholder = {
                    Text(text = "은행을 선택해주세요")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = onSurfaceLight,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                shape = RoundedCornerShape(15.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = bankAccount,
            onValueChange = onAccountChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = {
                Text(
                    text = "계좌번호를 입력해주세요",
                    style = Typography.bodyMedium,
                    color = onSurfaceLight
                )
            },
            shape = RoundedCornerShape(15.dp)
        )

        if (!error.isNullOrBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(text = error, color = MaterialTheme.colorScheme.error, style = Typography.bodySmall)
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onNextClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
            }
            Text("다음")
        }
    }
}
