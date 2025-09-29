package com.d108.moyeo.presentation.ui.screen.currency.exchange

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.screen.home.transfer.CurrencyData

@Composable
fun ChooseCurrencyContent(
    targetCurrencyName: String,
    paddingValues: PaddingValues,          // ✅ reservation과 동일
    selected: String?,
    currencies: List<CurrencyData>,
    onSelect: (String, String) -> Unit,    // (code, name)
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(20.dp)
    ) {
        Text(
            text = "${targetCurrencyName}화를 충전하기 위해\n사용할 통화를 선택해주세요",
            style = Typography.titleLarge
        )

        Spacer(Modifier.height(Spacing.Large))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(currencies) { c ->
                val isSelected = selected == c.code
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(15.dp)
                        )
                        .padding(12.dp)
                        .clickable { onSelect(c.code, c.name) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = c.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isSelected) Color.White else Color.Black
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = c.code,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) Color.White else Color.Black
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}
