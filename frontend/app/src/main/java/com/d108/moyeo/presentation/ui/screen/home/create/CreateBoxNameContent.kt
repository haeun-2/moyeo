package com.d108.moyeo.presentation.ui.screen.home.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.outlineLight

@Composable
fun CreateBoxNameContent(
    name: String,
    isLoading: Boolean,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        Text("박스 이름을\n입력해주세요", style = Typography.titleLarge)
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(
                    text = "예) 여름 도쿄 여행",
                    style = Typography.bodyMedium,
                    color = outlineLight
                )
            },
            shape = RoundedCornerShape(15.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onConfirm,
            enabled = !isLoading && name.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(bottom = Spacing.Medium)
        ) {
            Text(if (isLoading) "확인 중…" else "확인")
        }
    }
}
