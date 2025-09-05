package com.d108.moyeo.presentation.ui.component.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailBottomSheet(
    onDismiss: () -> Unit, // 취소(외부 클릭)
    onConfirm: (selectedCategory: String) -> Unit,  // 확인 버튼 클릭
    onCancel: () -> Unit  // 핸들 내리기
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true  // 걸쳐있는 동작 해제
    )
    val moyeoBoxes = remember { listOf("통장1", "통장2", "통장3", "통장4", "통장5", "통장 6") }
    var selectedMoyeoBoxes by remember { mutableStateOf(moyeoBoxes[0]) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },  // 시트 외부 클릭, 뒤로가기, 드래그 핸들로 내리기
        sheetState = sheetState
    ) {
        // 바텀시트 내부에 표시될 내용
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "카테고리 선택",
                style = Typography.titleLarge
            )
            Spacer(modifier = Modifier.height(20.dp))

            // WheelPicker 컴포저블 호출
            HistoryBoxWheelPicker(
                items = moyeoBoxes,
                onItemSelected = { category ->
                    selectedMoyeoBoxes = category // 선택된 아이템 상태 업데이트
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { onConfirm(selectedMoyeoBoxes) }) {
                Text("선택 완료")
            }
        }
    }
}