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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailBottomSheet(
    onDismiss: () -> Unit // 부모 Composable에서 닫기 이벤트를 처리하기 위한 람다
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
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
                text = "사용내역 상세",
                style = com.d108.moyeo.presentation.theme.Typography.titleLarge
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text("여기에 상세 내역 UI가 들어갑니다.")
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { onDismiss() }) {
                Text("닫기")
            }
        }
    }
}