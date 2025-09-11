package com.d108.moyeo.presentation.ui.component.home.mybox

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.screen.home.BoxFilterOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBoxFilterBottomSheet(
    initialFilters: BoxFilterOptions,
    onConfirm: (BoxFilterOptions) -> Unit,
    onDismiss: () -> Unit
) {
    // 시트 내부에서만 사용할 임시 상태 변수들. 초기값은 외부에서 받아옴.
    var tempPeriod by remember { mutableStateOf(initialFilters.period) }
    var tempScope by remember { mutableStateOf(initialFilters.scope) }
    var tempSort by remember { mutableStateOf(initialFilters.sort) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // 가장 위 오른쪽 끝에 확인 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    // 확인 버튼을 누르면, 임시로 저장했던 상태들로 새로운 FilterOptions 객체를 만들어 전달
                    onConfirm(BoxFilterOptions(period = tempPeriod, scope = tempScope, sort = tempSort))
                }) {
                    Text("확인", style = Typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 기간
            FilterSection(
                title = "기간",
                options = listOf("1개월", "3개월", "6개월", "직접 설정"),
                selectedOption = tempPeriod,
                onOptionSelected = { tempPeriod = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 유형 (모여박스에 맞게 옵션 변경)
            FilterSection(
                title = "유형",
                options = listOf("전체", "입금만", "출금만"),
                selectedOption = tempScope,
                onOptionSelected = { tempScope = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 정렬
            FilterSection(
                title = "정렬",
                options = listOf("최신순", "과거순"),
                selectedOption = tempSort,
                onOptionSelected = { tempSort = it }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// 각 필터 섹션을 위한 재사용 가능한 Composable
@Composable
private fun FilterSection(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = Typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = selectedOption == option
                OutlinedButton(
                    onClick = { onOptionSelected(option) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    ),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text(option)
                }
            }
        }
    }
}
