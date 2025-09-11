package com.d108.moyeo.presentation.ui.component.home.mywallet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.d108.moyeo.presentation.theme.Padding.HorizontalSmall
import com.d108.moyeo.presentation.theme.Padding.VerticalExtraSmall
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.onPrimaryContainerLight
import com.d108.moyeo.presentation.theme.onSurfaceLight
import com.d108.moyeo.presentation.theme.primaryContainerLight
import com.d108.moyeo.presentation.ui.screen.home.wallet.WalletFilterOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWalletFilterBottomSheet(
    initialFilters: WalletFilterOptions,
    onConfirm: (WalletFilterOptions) -> Unit,
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
                .padding(horizontal = HorizontalSmall, vertical = VerticalExtraSmall)
        ) {
            // 가장 위 오른쪽 끝에 확인 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    // 확인 버튼을 누르면, 임시로 저장했던 상태들로 새로운 FilterOptions 객체를 만들어 전달
                    onConfirm(WalletFilterOptions(period = tempPeriod, scope = tempScope, sort = tempSort))
                }) {
                    Text("확인", style = Typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(Spacing.Medium))

            FilterSection(
                title = "기간",
                options = listOf("1개월", "3개월", "6개월", "직접 설정"),
                selectedOption = tempPeriod,
                onOptionSelected = { tempPeriod = it }
            )

            Spacer(modifier = Modifier.height(Spacing.Large))

            // 유형
            FilterSection(
                title = "유형",
                options = listOf("전체", "입금만", "출금만"),
                selectedOption = tempScope,
                onOptionSelected = { tempScope = it }
            )

            Spacer(modifier = Modifier.height(Spacing.Large))

            // 정렬
            FilterSection(
                title = "정렬",
                options = listOf("최신순", "과거순"),
                selectedOption = tempSort,
                onOptionSelected = { tempSort = it }
            )
            Spacer(modifier = Modifier.height(Spacing.Large))
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
        Spacer(modifier = Modifier.height(Spacing.SmallMedium)) // 제목과 버튼 사이 간격 조정
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
        ) {
            options.forEach { option ->
                val isSelected = selectedOption == option

                OutlinedButton(
                    onClick = { onOptionSelected(option) },
                    // 각 항목들이 부모 영역을 등분해서 가짐
                    modifier = Modifier.weight(1f),
                    // 선택된 버튼에만 색상을 적용
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) primaryContainerLight else Color.Transparent,
                        contentColor = if (isSelected) onPrimaryContainerLight else onSurfaceLight
                    ),
                    // 버튼 내부 패딩을 조절하여 높이를 맞춤
                    contentPadding = PaddingValues(Spacing.SmallMedium)
                ) {
                    // 그리고 버튼 내 글자들은 가운데에 정렬됨
                    Text(option)
                }
            }
        }
    }
}
