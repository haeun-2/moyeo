package com.d108.moyeo.presentation.ui.screen.home.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.d108.moyeo.R
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun CreateBoxResultContent(
    inviteLink: String?,
    expiresAt: String?,
    onCopyOnly: () -> Unit,
    onCopyAndClose: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("박스를 만들었어요!", style = Typography.titleLarge)

            Spacer(Modifier.height(36.dp))

            AssistChip(
                onClick = { onCopyOnly() },
                label = {
                    Text(
                        text = inviteLink ?: "초대 링크가 없습니다",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_link_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.ExtraLarge)
            )

            Spacer(Modifier.height(36.dp))

            Text("같이 사용할 사람에게", style = Typography.bodyMedium)

            Spacer(Modifier.height(8.dp))

            Text("초대 링크를 보내주세요", style = Typography.bodyMedium)
        }

        expiresAt?.let {
            Text("$it 이후 만료", style = Typography.bodyMedium)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onCopyAndClose,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(bottom = Spacing.Medium)
        ) {
            Text("링크 복사하고 닫기")
        }
    }
}
