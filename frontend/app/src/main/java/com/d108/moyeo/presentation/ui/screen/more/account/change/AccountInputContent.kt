package com.d108.moyeo.presentation.ui.screen.more.account.change

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.onSurfaceLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountInputContent(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    selectedBankName: String,
    bankAccount: String,
    isLoading: Boolean,
    error: String?,
    onSelectBankClick: () -> Unit,
    onAccountChange: (String) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("계좌번호 입력") },
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
            Text("연결할 계좌번호를\n입력해주세요", style = Typography.titleLarge)
            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = selectedBankName.ifBlank { "은행을 선택해주세요" },
                onValueChange = { /* no-op */ },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = onSelectBankClick) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "은행 선택")
                    }
                },
                shape = RoundedCornerShape(15.dp)
            )

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

            Spacer(Modifier.height(24.dp))

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
}
