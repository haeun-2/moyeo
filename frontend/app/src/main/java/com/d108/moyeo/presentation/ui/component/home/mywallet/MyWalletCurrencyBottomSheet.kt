package com.d108.moyeo.presentation.ui.component.home.mywallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.onSurfaceLight

// 임시 데이터 클래스
data class Currency(val code: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWalletCurrencyBottomSheet(
    currencies: List<Currency>,
    onItemSelected: (Currency?) -> Unit, // null이면 '전체 보기'를 의미
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // --- 스크롤 제어 로직이 필요하지 않아 제거함 ---

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
                columns = GridCells.Fixed(2), // 2열 그리드
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
                verticalArrangement = Arrangement.spacedBy(Spacing.Medium),
                contentPadding = PaddingValues(
                    vertical = Spacing.Medium
                ),
            ) {
                // 맨 처음, 그러니까 0행 0열에는 전체 보기
                item {
                    CurrencyButton(
                        text = "전체 보기",
                        onClick = { onItemSelected(null) },
                    )
                }
                // 나머지는 2열 그리드
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

// 그리드 아이템을 위한 공용 버튼 Composable
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
        Text(
            text = text,
            textAlign = TextAlign.Center
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun MyWalletCurrencyBottomSheetPreview() {
    // 우리가 한화 1개 + 외화 7개를 제공할 거임
    val sampleCurrencies = listOf(
        Currency("KRW", "대한민국 원"),
        Currency("USD", "미국 달러"),
        Currency("JPY", "일본 엔"),
        Currency("EUR", "유럽 유로"),
        Currency("CNY", "중국 위안"),
        Currency("GBP", "영국 파운드"),
        Currency("CAD", "캐나다 달러"),
        Currency("AUD", "호주 달러")
    )
    MyWalletCurrencyBottomSheet(
        currencies = sampleCurrencies,
        onItemSelected = {},
        onDismiss = {}
    )
}