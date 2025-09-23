package com.d108.moyeo.presentation.ui.screen.home.box.calculate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.onPrimaryLight
import com.d108.moyeo.presentation.theme.onSurfaceVariantLight
import com.d108.moyeo.presentation.theme.primaryLight
import com.d108.moyeo.presentation.theme.surfaceVariantLight
import com.d108.moyeo.presentation.ui.component.home.Currency
import com.d108.moyeo.presentation.ui.screen.home.transfer.CurrencyData
import com.d108.moyeo.util.CurrencyUtils.getCurrencyName

@Composable
fun ChooseCurrencyContent(
    availableCurrencies: List<Currency>,
    selectedCurrencies: Set<String>,
    onCurrencySelect: (String) -> Unit
) {
    Column {
        Text(
            text = "어떤 통화를 정산할까요?",
            style = Typography.titleLarge
        )
        Spacer(Modifier.height(Spacing.Large))

        if (availableCurrencies.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("정산할 수 있는 통화가 없습니다.")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
                verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
            ) {
                items(availableCurrencies) { currency ->
                    val isSelected = selectedCurrencies.contains(currency.code)
                    val currencyName = getCurrencyName(currency.code)
                    CurrencyButton(
                        text = "${currencyName}\n(${currency.code})",
                        isSelected = isSelected,
                        onClick = { onCurrencySelect(currency.code) }
                    )
                }
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