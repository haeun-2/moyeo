package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.*
import com.d108.moyeo.presentation.ui.screen.home.transfer.TargetBoxContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxSelectionScreen(
    navController: NavController,
    viewModel: BoxSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val boxes by viewModel.availableBoxes.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Padding.Content)
    ) {
        // 상단 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Color.Black
                )
            }
            Text(
                text = "통장 선택",
                style = Typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "환전할 원화가 출금될 통장을 선택해주세요",
            style = Typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 기존 TargetBoxContent 재사용
        Box(modifier = Modifier.weight(1f)) {
            TargetBoxContent(
                selectedBoxId = uiState.selectedBoxId,
                onBoxSelect = viewModel::onBoxSelected,
                boxes = boxes,
            )
        }

        // 다음 버튼
        Button(
            onClick = {
                if (uiState.selectedBoxId != -1L) {
                    navController.navigate("currency_selection?boxId=${uiState.selectedBoxId}")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = uiState.selectedBoxId != -1L,
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = "다음",
                color = Color.White,
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}