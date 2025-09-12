package com.d108.moyeo.presentation.ui.component.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.onSurfaceLight

/** 앱 전반에서 사용하는 통화 모델 (Wallet/Box 공용). */
data class Currency(
    val code: String, // 예: "KRW", "USD"
    val name: String  // 예: "대한민국 원", "미국 달러"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyBottomSheet(
    currencies: List<Currency>,
    onItemSelected: (Currency?) -> Unit, // null이면 '전체 보기'
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(horizontal = Padding.HorizontalSmall, vertical = Padding.VerticalSmall),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
                verticalArrangement = Arrangement.spacedBy(Spacing.Medium),
                contentPadding = PaddingValues(vertical = Spacing.Medium),
            ) {
                // 0행 0열: 전체 보기
                item {
                    CurrencyButton(
                        text = "전체 보기",
                        onClick = { onItemSelected(null) }
                    )
                }
                // 나머지 통화들
                items(currencies) { currency ->
                    CurrencyButton(
                        text = "${currency.name}\n(${currency.code})",
                        onClick = { onItemSelected(currency) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrencyButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(width = 160.dp, height = 100.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = onSurfaceLight
        )
    ) {
        Text(text = text, textAlign = TextAlign.Center)
    }
}