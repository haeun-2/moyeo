package com.d108.moyeo.presentation.ui.screen.home.creating

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateBoxScreen(
    onBackClick: () -> Unit,
    onFinishClick: () -> Unit,
    viewModel: CreateBoxViewModel = hiltViewModel()
) {
    val ui = viewModel.uiState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { ev ->
            when (ev) {
                is CreateBoxEvent.NavigateResult -> viewModel.moveToResult(ev.boxId)
                is CreateBoxEvent.ShowError      -> snackbarHostState.showSnackbar(ev.message)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // 상단 바 (뒤로가기)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "back")
            }
        }

        // 본문 영역
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            when (ui.currentStep) {
                CreateBoxStep.NAME -> {
                    CreateBoxNameContent(
                        name = ui.name,
                        isLoading = ui.isLoading,
                        onNameChange = viewModel::onNameChange,
                        onConfirm = { viewModel.onConfirmClick() }
                    )
                }

                CreateBoxStep.RESULT -> {
                    CreateBoxResultContent(
                        boxId = ui.createdBoxId,
                        onFinishClick = onFinishClick
                    )
                }
            }
        }
//        SnackbarHost(
//            hostState = snackbarHostState,
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(bottom = 12.dp)
//        )
    }
}
