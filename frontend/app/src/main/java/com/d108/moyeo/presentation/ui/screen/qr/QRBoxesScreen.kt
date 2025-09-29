package com.d108.moyeo.presentation.ui.screen.qr

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.common.LazyColumnMoyeoBoxesItem

@Composable
fun QRBoxesScreen(
    navController: NavController,
    viewModel: QRBoxesViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsState()
    val boxes by viewModel.paymentBoxes.collectAsState()

    BackHandler { viewModel.onBackClick() }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is QRBoxesNavEvent.NavigateBackWithResult -> {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("newly_bookmarked_id", event.selectedBoxId)
                    navController.popBackStack()
                }
                is QRBoxesNavEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.Medium)
            ) {
                IconButton(
                    onClick = { viewModel.onBackClick() },
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "뒤로가기"
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = Spacing.ExtraLarge,
                    end = Spacing.ExtraLarge,
                )
        ) {
            Text(
                text = "추가할 모여 박스를\n선택해주세요",
                style = Typography.titleLarge
            )

            Spacer(modifier = Modifier.height(Spacing.Large))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator()
                    }
                    uiState.errorMessage != null -> {
                        Text(text = uiState.errorMessage!!)
                    }
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(Spacing.Medium),
                            modifier = Modifier.fillMaxWidth()
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
                            item { Spacer(modifier = Modifier.height(Spacing.Medium)) }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.Medium))

            // 하단 고정 확인 버튼 (QRScreen/TransferScreen 가이드와 동일)
            Button(
                onClick = viewModel::onConfirmClick,
                enabled = uiState.newlySelectedBoxId != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(bottom = Spacing.Medium)
            ) {
                Text("확인")
            }
        }
    }
}