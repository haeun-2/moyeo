package com.d108.moyeo.presentation.ui.screen.home.join

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun JoinBoxContent(
    uiState: JoinUiState,
    onJoinCodeChange: (String) -> Unit,
    onJoinClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = uiState.joinCode,
            onValueChange = onJoinCodeChange,
            label = {
                Text(
                    "초대 코드 입력",
                    style = Typography.labelLarge.copy(
                        fontSize = 14.sp,
                        lineHeight = TextUnit.Unspecified,
                        letterSpacing = 0.sp
                    )
                )
            },
            textStyle = Typography.bodyLarge.copy(
                fontSize = 16.sp,
                lineHeight = TextUnit.Unspecified,
                letterSpacing = 0.sp
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onJoinClick,
            enabled = uiState.joinCode.isNotBlank()
        ) {
            Text("참여하기")
        }
    }
}
