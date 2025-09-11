package com.d108.moyeo.presentation.ui.screen.home.sending

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.onPrimaryLight
import com.d108.moyeo.presentation.theme.onSurfaceVariantLight
import com.d108.moyeo.presentation.theme.primaryLight
import com.d108.moyeo.presentation.theme.surfaceVariantLight

// 화면에 표시할 통화 데이터 클래스 (ViewModel에서 전달받을 데이터 모델)
data class CurrencyData(
    val name: String, // "대한민국 원"
    val code: String  // "KRW"
)

@Composable
fun ChooseCurrencyContent(
    selectedCurrency: String,
    onCurrencySelect: (String) -> Unit
) {
    // TODO: 이 더미 데이터는 추후 ViewModel에서 실제 보유 통화 목록으로 받아와야 합니다.
    val dummyCurrencies = listOf(
        CurrencyData("대한민국 원", "KRW"),
        CurrencyData("미국 달러", "USD"),
        CurrencyData("유럽 유로", "EUR"),
        CurrencyData("일본 엔", "JPY"),
        CurrencyData("중국 위안", "CNY"),
        CurrencyData("영국 파운드", "GBP")
    )

    Column {
        Text(
            text = "어떤 통화로 보낼까요?",
            style = Typography.titleLarge
        )
        Spacer(Modifier.height(Spacing.Large))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2열 그리드
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
        ) {
            items(dummyCurrencies) { currency ->
                val isSelected = (currency.code == selectedCurrency)
                CurrencyButton(
                    text = "${currency.name}\n(${currency.code})",
                    isSelected = isSelected,
                    onClick = { onCurrencySelect(currency.code) }
                )
            }
        }
    }
}

// 그리드 아이템을 위한 공용 버튼 Composable (선택 상태 반영 로직 추가)
@Composable
private fun CurrencyButton(
    text: String,
    isSelected: Boolean, // 현재 선택되었는지 여부를 받는 파라미터
    onClick: () -> Unit
) {
    // 선택되었을 때와 아닐 때의 색상을 다르게 지정
    val colors = if (isSelected) {
        // 선택된 경우: Primary 색상 (앱의 주요 색상)
        ButtonDefaults.buttonColors(
            containerColor = primaryLight,
            contentColor = onPrimaryLight
        )
    } else {
        // 선택되지 않은 경우: 기본 Surface 색상
        ButtonDefaults.buttonColors(
            containerColor = surfaceVariantLight,
            contentColor = onSurfaceVariantLight
        )
    }

    Button(
        onClick = onClick,
        modifier = Modifier.size(width = 160.dp, height = 100.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        colors = colors // 위에서 결정된 색상 적용
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = Typography.bodyLarge
        )
    }
}