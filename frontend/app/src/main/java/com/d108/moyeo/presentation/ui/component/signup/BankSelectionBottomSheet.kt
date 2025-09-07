package com.d108.moyeo.presentation.ui.component.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankSelectionBottomSheet(
    banks: List<String>,
    onBankSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        // 바텀시트 내용 전체를 중앙 정렬하기 위한 Column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f) // 최대 높이를 80%로 설정
                .padding(vertical = 24.dp), // 상하 여백만 지정
            horizontalAlignment = Alignment.CenterHorizontally // 자식(그리드)을 수평 중앙에 배치
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2열 그리드
                horizontalArrangement = Arrangement.spacedBy(16.dp), // 아이템 간 가로 간격
                verticalArrangement = Arrangement.spacedBy(16.dp)   // 아이템 간 세로 간격
            ) {
                items(banks) { bank ->
                    OutlinedButton(
                        onClick = { onBankSelected(bank) },
                        modifier = Modifier.size(width = 160.dp, height = 100.dp)
                    ) {
                        Text(
                            text = bank,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}