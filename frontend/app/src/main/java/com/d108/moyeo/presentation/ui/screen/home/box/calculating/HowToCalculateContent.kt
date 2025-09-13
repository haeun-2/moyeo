package com.d108.moyeo.presentation.ui.screen.home.box.calculating

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.primaryLight




@Composable
fun HowToCalculateContent(viewModel: CalculatingViewModel) {

    val uiState by viewModel.uiState.collectAsState()

    // 임시 데이터
    val participants = uiState.participants

    Column {
        // 이전 단계에서 받아온 아이디를 표시
        Text("정산할 박스 ID: ${uiState.boxId}",
            style = typography.labelMedium, color = Color.Gray)

        Spacer(modifier = Modifier.height(Spacing.Small))

        // 테두리가 있는 박스로 , 왼쪽에는 정산예정 화폐가 표시됨. 오른쪽엔 금액 + 화폐단위가 표시됨. 일단 임의로 값만 처박아
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.Medium, vertical = Spacing.Small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("정산 예정", style = typography.bodyLarge)
                Spacer(modifier = Modifier.weight(1f))
                Text("50,000 JPY", style = typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }

        // 여백 << 이 새끼가 다 처먹고
        Spacer(modifier = Modifier.weight(1f))

        // 참가자들의 명단이 레이지컬럼으로 있음
        LazyColumn( // << 이게 아래쪽에 다 처박혀야 함
            verticalArrangement = Arrangement.spacedBy(Spacing.Small)
        ) {
            items(participants) { participant ->
                ParticipantRow(
                    participant = participant,
                    onCheckedChange = { isChecked ->
                        viewModel.onParticipantSelectionChanged(participant.id, isChecked)
                    }
                )
            }
        }
    }
}

@Composable
private fun ParticipantRow(participant: CalculatingParticipant, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 각 아이템은 X, 이름, 여백, 돈 / 인원수, 화폐단위, 그리고 수정 버튼

        // X 버튼
        Checkbox    (
            checked = participant.isSelected,
            onCheckedChange = onCheckedChange
        )

        // 이름
        Text(
            text = participant.name,
            style = typography.bodyLarge,
            modifier = Modifier.padding(start = Spacing.Small)
        )

        // 여백
        Spacer(modifier = Modifier.weight(1f))

        // 돈 / 인원수, 화폐단위
        Text(
            text = "${participant.amount} ${participant.currency}",
            style = typography.bodyLarge
        )

        // 수정 버튼
        IconButton(
            onClick = {
                /* TODO: 금액 수정 로직 */
            },
            modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "금액 수정", tint = primaryLight)
        }
    }
}