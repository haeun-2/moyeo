package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.surfaceVariantLight
import com.d108.moyeo.presentation.theme.primaryLight

@Composable
fun CurrencySelectionScreen(
    navController: NavController,
    viewModel: CurrencySelectionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                text = "어떤 통화를 예약하고 싶나요?",
                style = Typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(Spacing.Large))

        // 통화 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
        ) {
            items(uiState.currencies) { currency ->
                CurrencyCard(
                    currency = currency,
                    isSelected = uiState.selectedCurrency == currency,
                    onClick = { viewModel.selectCurrency(currency) }
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.Large))

        // 확인 버튼
        Button(
            onClick = {
                viewModel.getSelectedCurrency()?.let {
                    // TODO: 선택된 통화로 예약 화면으로 이동
                    navController.popBackStack()
                }
            },
            enabled = uiState.selectedCurrency != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            // 나라 선택시 확인 버튼 색 변경
            colors = ButtonDefaults.buttonColors(
                containerColor = if (uiState.selectedCurrency != null) primaryLight else Color.Gray,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                disabledContentColor = Color.Gray
            )
        ) {
            Text(
                text = "확인",
                color = Color.White,
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Medium))
    }
}

@Composable
private fun CurrencyCard(
    currency: Currency,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                color = if (isSelected) primaryLight else surfaceVariantLight,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(Spacing.Medium),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currency.flag,
                style = Typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(Spacing.Small))
            Text(
                text = currency.name,
                style = Typography.bodyMedium,
                color = if (isSelected) Color.White else Color.Black,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}