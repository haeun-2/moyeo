package com.d108.moyeo.presentation.ui.screen.currency.exchange

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.CustomKeypad
import com.d108.moyeo.presentation.ui.component.KeyMode
import com.d108.moyeo.presentation.ui.component.KeypadKey
import java.text.NumberFormat
import java.util.Locale

// HowMuchContent.kt

@Composable
fun HowMuchContent(
    mode: ExchangeMode,
    chargingCurrencyName: String,
    chargingCurrencyCode: String,
    spendingCurrencyName: String,
    spendingCurrencyCode: String,
    amount: String,
    onKeyPress: (KeypadKey) -> Unit,
    availableBalance: Double,
    requiredSpendAmount: Double,
    isMultiStepExchange: Boolean,
    rateForStep1: Double,
    rateForStep2: Double
) {
    // 포맷터들을 함수 내부에 정의
    val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
    val krwFormat = NumberFormat.getCurrencyInstance(Locale.KOREA).apply {
        maximumFractionDigits = 0
    }
    val isBalanceEnough = requiredSpendAmount <= availableBalance
    val titleText = if (mode == ExchangeMode.REFUND) {
        "얼마를 돌려받을까요?"
    } else {
        "${chargingCurrencyName}화를\n얼마나 충전할까요?"
    }

    val amountUnitText = if (mode == ExchangeMode.REFUND) {
        "원"
    } else {
        chargingCurrencyName
    }
    Column {
        Text(text = titleText, style = Typography.titleLarge) // << 수정된 텍스트 적용
        Spacer(Modifier.height(Spacing.Medium))

        // 사용자 입력 금액
        Text(
            text = numberFormat.format(amount.toLongOrNull() ?: 0L),
            style = Typography.displayLarge,
            fontWeight = FontWeight.Bold
        )
        Text(text = amountUnitText, style = Typography.bodyLarge) // << 수정된 단위 적용
        Spacer(Modifier.height(Spacing.Medium))

        // 환율 정보 (isMultiStepExchange 값에 따라 동적으로 표시)
        if (isMultiStepExchange) {
            if (rateForStep1 > 0 && rateForStep2 > 0) {
                // ✨ JPY인 경우 '100 JPY'로, 아니면 통화 코드를 그대로 사용
                val unitText1 = if (spendingCurrencyCode == "JPY") "100 JPY" else spendingCurrencyCode
                val unitText2 = if (chargingCurrencyCode == "JPY") "100 JPY" else chargingCurrencyCode

                Text(
                    text = "적용 환율 1: ${String.format("%,.2f", rateForStep1)} KRW / $unitText1",
                    style = Typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "적용 환율 2: ${String.format("%,.2f", rateForStep2)} KRW / $unitText2",
                    style = Typography.bodySmall,
                    color = Color.Gray
                )
            }
        } else {
            if (rateForStep1 > 0) {
                val baseCurrency = if (mode == ExchangeMode.REFUND) {
                    spendingCurrencyCode // JPY
                } else {
                    if(spendingCurrencyCode == "KRW") chargingCurrencyCode else spendingCurrencyCode
                }
                val unitText = if (baseCurrency == "JPY") "100 JPY" else baseCurrency
                val rateText = if (baseCurrency == "JPY") String.format("%,.2f", rateForStep1 * 100) else String.format("%,.2f", rateForStep1)

                Text(
                    text = "적용 환율: $rateText KRW / $unitText",
                    style = Typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(Modifier.height(Spacing.Large))

        // 필요한 금액
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("필요한 $spendingCurrencyName", style = Typography.bodyMedium, color = Color.Gray)
            Text(
                text = if(spendingCurrencyCode == "KRW") krwFormat.format(requiredSpendAmount.toLong())
                else numberFormat.format(requiredSpendAmount.toLong()),
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (!isBalanceEnough && amount != "0") Color.Red else Color.Black
            )
        }
        Spacer(Modifier.height(Spacing.Small))

        // 보유 잔액
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("보유 $spendingCurrencyName", style = Typography.bodyMedium, color = Color.Gray)
            Text(
                text = if(spendingCurrencyCode == "KRW") krwFormat.format(availableBalance.toLong())
                else numberFormat.format(availableBalance.toLong()),
                style = Typography.bodyMedium
            )
        }

        Spacer(Modifier.weight(1f))

        CustomKeypad(
            onKeyPress = onKeyPress,
            keypadColortype = "normal",
            keyMode = KeyMode.Zeros,
            buttonAspectRatio = 1.2f,
            modifier = Modifier.padding(horizontal = Spacing.Large).height(320.dp)
        )
    }
}