package com.d108.moyeo.presentation.ui.screen.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.common.GridMoyeoBoxesItem
import com.d108.moyeo.presentation.ui.component.history.HistoryBoxesItem

@Composable
fun HistoryBoxesScreen(
    navController: NavController,
    viewModel: HistoryBoxesViewModel = hiltViewModel()
) {
    // ViewModel의 상태를 구독
    val uiState by viewModel.uiState.collectAsState()


    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is HistoryBoxesNavEvent.NavigateBackWithResult -> {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_box_id_for_history", event.selectedBoxId)
                    navController.popBackStack()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = Spacing.Medium, end = Spacing.Medium,
                top = Padding.ScreenTop, bottom = Padding.ScreenBottom
            ),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "내 모여 박스 목록",
                style = Typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Large))

        Box(
            modifier = Modifier.weight(1f), // Box가 남은 공간을 차지하도록
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.errorMessage != null) {
                Text(text = uiState.errorMessage!!)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
                ) {
                    items(
                        items = uiState.allBoxes,
                        key = { it.id }
                    ) { box ->
                        HistoryBoxesItem(
                            box = box,
                            // isSelected 상태를 ViewModel의 newlySelectedBoxId와 비교하여 결정
                            isSelected = (uiState.newlySelectedBoxId == box.id),
                            onClick = { viewModel.onBoxSelected(box.id) }
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