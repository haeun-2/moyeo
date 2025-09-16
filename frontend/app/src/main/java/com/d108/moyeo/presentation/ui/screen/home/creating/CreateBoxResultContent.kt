package com.d108.moyeo.presentation.ui.screen.home.creating

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun CreateBoxResultContent(
    boxId: Long?,
    onFinishClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("박스를 만들었어요", style = Typography.titleLarge)
        Spacer(Modifier.height(24.dp))
        Text("https://moyeo.app/box/$boxId", style = Typography.titleMedium)
        Spacer(Modifier.height(24.dp))
        Text("같이 사용할 사람에게 해당 링크를 보내요")
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onFinishClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(52.dp)
        ) {
            Text("닫기")
        }
    }
}
