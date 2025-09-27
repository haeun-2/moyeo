package com.d108.moyeo.presentation.ui.screen.currency.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class ReservationCurrency(val code: String, val name: String, val flag: String)

@Composable
fun ChooseCurrencyContent(
    paddingValues: PaddingValues,
    selected: String?,
    onSelect: (String, String) -> Unit,
) {
    val currencies = remember {
        listOf(
            ReservationCurrency("USD","미국 달러","🇺🇸"),
            ReservationCurrency("JPY","일본 엔","🇯🇵"),
            ReservationCurrency("EUR","유럽 유로","🇪🇺"),
            ReservationCurrency("GBP","영국 파운드","🇬🇧"),
            ReservationCurrency("CNY","중국 위안","🇨🇳"),
            ReservationCurrency("CAD","캐나다 달러","🇨🇦"),
            ReservationCurrency("AUD","호주 달러","🇦🇺"),
            ReservationCurrency("CHF","스위스 프랑","🇨🇭"),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(20.dp)
    ) {
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
                        Text(c.flag, style = MaterialTheme.typography.headlineLarge)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = c.name,
                            color = if (isSelected) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}
