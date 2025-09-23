package com.d108.moyeo.presentation.ui.screen.home.box.calculate

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.d108.moyeo.domain.model.box.BoxMember
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.primaryLight
import java.text.DecimalFormat


@Composable
fun HowToCalculateContent(
    uiState: CalculateUiState,
    onParticipantSelectionChanged: (Long, Boolean) -> Unit,
    onParticipantAmountChanged: (Long, String) -> Unit
) {
    val currentCurrency = uiState.settlementQueue.getOrNull(uiState.currentSettlementIndex) ?: ""
    val formattedTotalAmount = DecimalFormat("#,###.##").format(uiState.totalSettlementAmount)

    Column {
        Text(
            "정산할 박스 ID: ${uiState.boxInfo?.id ?: "null"}",
            style = typography.labelMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(Spacing.Small))

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
                Text(
                    text = "$formattedTotalAmount $currentCurrency",
                    style = typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.Small)
        ) {
            items(uiState.settlementParticipants) { participant ->
                ParticipantRow(
                    participant = participant,
                    isSelected = uiState.selectedMemberIds.contains(participant.member.id),
                    selectedCount = uiState.selectedMemberIds.size,
                    onCheckedChange = { isChecked ->
                        onParticipantSelectionChanged(participant.member.id, isChecked)
                    },
                    onAmountChange = { newAmountStr ->
                        onParticipantAmountChanged(participant.member.id, newAmountStr)
                    }
                )
            }
        }
    }
}

@Composable
private fun ParticipantRow(
    participant: SettlementParticipant,
    isSelected: Boolean,
    selectedCount: Int,
    onCheckedChange: (Boolean) -> Unit,
    onAmountChange: (String) -> Unit
) {
    // 편집 가능 여부 계산
    val isEditable = isSelected && participant.isEnabled && selectedCount > 1
    var isFocused by remember { mutableStateOf(false) }

    val displayValue = if (isFocused) {
        // 편집 중(포커스됨): 콤마 없는 원본 숫자를 보여줌
        // participant.amountStr.replace(",", "") 보다 아래 방식이 더 안전합니다.
        DecimalFormat("0.####").format(participant.amount)
    } else {
        // 평상시(포커스 없음): ViewModel에서 받은 포맷된 문자열을 보여줌
        participant.amountStr
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = onCheckedChange
        )

        Text(
            text = participant.member.name,
            style = typography.bodyLarge,
            modifier = Modifier.padding(start = Spacing.Small),
            color = if (isSelected) Color.Unspecified else Color.Gray
        )

        Spacer(modifier = Modifier.size(Spacing.Small))

        // 개선된 입력 필드
        Box(
            modifier = Modifier.weight(1f)
        ) {
            if (isEditable) {
                // 편집 가능한 경우: BasicTextField 사용
                BasicTextField(
                    value = displayValue,
                    onValueChange = onAmountChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused }
                        .background(
                            color = if (isFocused) Color.White else Color(0xFFF8F9FA),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    textStyle = TextStyle(
                        fontSize = typography.bodyLarge.fontSize,
                        color = Color.Black
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                innerTextField()
                            }
                        }
                    }
                )
            } else {
                // 편집 불가능한 경우: 시각적으로 명확하게 구분
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF0F0F0),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = displayValue,
                        style = typography.bodyLarge,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )

                    // 잠금 아이콘으로 편집 불가능함을 명확히 표시
                    if (!isSelected || selectedCount == 1) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "편집 불가",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}