package com.d108.moyeo.presentation.ui.screen.qr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.common.LazyColumnMoyeoBoxesItem

@Composable
fun QRBoxesScreen(
    navController: NavController,
    viewModel: QRBoxesViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsState()
    val boxes by viewModel.groupBoxesUi.collectAsState()


    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is QRBoxesNavEvent.NavigateBackWithResult -> {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("newly_bookmarked_id", event.selectedBoxId)
                    navController.popBackStack()
                }
            }
        }
    }

    Column(
        modifier = Modifier.padding(
            start = Spacing.Medium, end = Spacing.Medium,
            top = Padding.ScreenTop, bottom = Padding.ScreenBottom
        ),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center // 중앙 정렬
        ) {
            Text(
                text = "내 모여 박스 목록",
                style = Typography.titleLarge
            )
        }

        // 약간의 스페이서
        Spacer(modifier = Modifier.height(Spacing.Large))

        Box(
            modifier = Modifier.weight(1f), // Box가 남은 공간을 모두 차지하도록
            contentAlignment = Alignment.Center // 내용물을 중앙에 배치
        ) {

            if (uiState.isLoading) {  // 로딩 중
                CircularProgressIndicator()
            }

            else if (uiState.errorMessage != null) {  // 에러 발생
                Text(text = uiState.errorMessage!!)
            }

            else {  // 성공
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
                ) {
                    items(
                        items = boxes,
                        key = { it.id }
                    ) { box ->
                        LazyColumnMoyeoBoxesItem(
                            box = box,
                            isSelected = (uiState.newlySelectedBoxId == box.id),
                            onClick = { viewModel.onBoxClick(box) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.Medium))

        Button(
            onClick = viewModel::onConfirmClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.newlySelectedBoxId != null
        ) {
            Text("확인")
        }
    }
}
