package com.d108.moyeo.presentation.ui.component.exchange.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.onPrimaryLight
import com.d108.moyeo.presentation.theme.onSurfaceVariantLight
import com.d108.moyeo.presentation.theme.primaryLight
import com.d108.moyeo.presentation.theme.surfaceVariantLight

/**
 * 환율 히스토리 화면의 컨트롤 패널 UI
 * @param tradeMode 현재 선택된 거래 모드 ("buy" or "sell")
 * @param selectedTimeUnit 현재 선택된 시간 단위 ("10m", "1h", "1d")
 * @param onTradeModeChange 거래 모드 변경 시 호출될 콜백
 * @param onTimeUnitChange 시간 단위 변경 시 호출될 콜백
 */
@Composable
fun ExchangeHistoryControlPanel(
    tradeMode: String,
    selectedTimeUnit: String,
    onTradeModeChange: (String) -> Unit,
    onTimeUnitChange: (String) -> Unit
) {
    Column {
        // '매수' / '매도' 선택 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
        ) {
            ToggleButton(
                text = "사실 때",
                isSelected = tradeMode == "buy",
                onClick = { onTradeModeChange("buy") }
            )
            ToggleButton(
                text = "파실 때",
                isSelected = tradeMode == "sell",
                onClick = { onTradeModeChange("sell") }
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Small))

        // '10분' / '1시간' / '1일' 선택 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
        ) {
            ToggleButton(
                text = "10분",
                isSelected = selectedTimeUnit == "10m",
                onClick = { onTimeUnitChange("10m") }
            )
            ToggleButton(
                text = "1시간",
                isSelected = selectedTimeUnit == "1h",
                onClick = { onTimeUnitChange("1h") }
            )
            ToggleButton(
                text = "1일",
                isSelected = selectedTimeUnit == "1d",
                onClick = { onTimeUnitChange("1d") }
            )
        }
    }
}

/**
 * 선택 여부에 따라 색상이 변경되는 재사용 가능한 버튼
 * @param text 버튼에 표시될 텍스트
 * @param isSelected 버튼의 선택 여부
 * @param onClick 버튼 클릭 시 호출될 콜백
 */
@Composable
private fun ToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = if (isSelected) {
            // 선택된 상태의 색상
            ButtonDefaults.buttonColors(
                containerColor = primaryLight,
                contentColor = onPrimaryLight,
            )
        } else {
            // 선택되지 않은 상태의 색상
            ButtonDefaults.buttonColors(
                containerColor = surfaceVariantLight,
                contentColor = onSurfaceVariantLight,
            )
        }
    ) {
        Text(text = text)
    }
}