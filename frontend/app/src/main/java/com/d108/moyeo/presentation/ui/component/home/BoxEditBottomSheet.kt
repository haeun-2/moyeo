package com.d108.moyeo.presentation.ui.component.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxEditBottomSheet(
    initialName: String,
    initialColor: Color,
    availableColors: List<Color>,
    onConfirm: (name: String, color: Color) -> Unit,
    onDismiss: () -> Unit
) {
    var currentName by remember { mutableStateOf(initialName) }
    var selectedColor by remember { mutableStateOf(initialColor) }
    var isEditingName by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
//        confirmValueChange = { false } // 드래그로 닫힘 방지
    )

    ModalBottomSheet(
        onDismissRequest = {
            currentName = initialName
            selectedColor = initialColor
            isEditingName = false
            onDismiss()
        },
        containerColor = Color.White,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Large, vertical = Spacing.Medium)
        ) {
            // 이름 편집
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable (
                        enabled = !isEditingName,
                        onClick = { isEditingName = true }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(onClick = { isEditingName = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "이름 수정")
                    }
                }
            }

            Spacer(Modifier.height(Spacing.Large))

            // 컬러칩 그리드
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
                verticalArrangement = Arrangement.spacedBy(Spacing.Small)
            ) {
                items(availableColors) { color ->
                    val isSelected = color == selectedColor
                    Surface(
                        modifier = Modifier
                            .size(64.dp) // 칩 크기
                            .padding(8.dp)
                            .clickable { selectedColor = color },
                        shape = CircleShape, // 원형 모양
                        color = color,
//                        border = if (color == selectedColor) {
//                            BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
//                        } else {
//                            BorderStroke(0.5.dp, Color.LightGray)
//                        }
                        border = BorderStroke(0.5.dp, Color.LightGray)
                    ) {
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "선택됨",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(Spacing.Large))

            // 확인 버튼
            Button(
                onClick = { onConfirm(currentName, selectedColor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.Medium)
            ) {
                Text("확인")
            }
        }
    }
}
