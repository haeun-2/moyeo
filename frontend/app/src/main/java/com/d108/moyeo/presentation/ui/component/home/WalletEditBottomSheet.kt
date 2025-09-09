package com.d108.moyeo.presentation.ui.component.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.primaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletEditBottomSheet(
    initialName: String,
    initialColor: Color,
    availableColors: List<Color>,
    onConfirm: (name: String, color: Color) -> Unit,
    onDismiss: () -> Unit
) {
    // 시트 내부에서 임시로 사용할 상태. 확인 버튼을 눌러야 실제 데이터가 변경
    var currentName by remember { mutableStateOf(initialName) }
    var selectedColor by remember { mutableStateOf(initialColor) }
    var isEditingName by remember { mutableStateOf(false) }

    // 사용자가 드래그해서 닫는 것을 방지
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { false } // 모든 제스처로 인한 상태 변경을 거부
    )


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // 가장 위 오른쪽 끝에 확인 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { onConfirm(currentName, selectedColor) }) {
                    Text("확인")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 이름 수정 영역
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 편집 모드에 따라 TextField 또는 Text를 보여줍니다.
                if (isEditingName) {
                    OutlinedTextField(
                        value = currentName,
                        onValueChange = { currentName = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(onClick = { isEditingName = false }) {
                        Icon(Icons.Default.Check, contentDescription = "수정 완료")
                    }
                } else {
                    Text(
                        text = currentName,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { isEditingName = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "이름 수정")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3행 4열의 컬러칩 그리드
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(availableColors) { color ->
                    val isSelected = color == selectedColor
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { selectedColor = color }
                            // 선택된 요소는 하이라이트(테두리)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) primaryLight else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // 선택되면 체크 아이콘 표시
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "선택됨",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}