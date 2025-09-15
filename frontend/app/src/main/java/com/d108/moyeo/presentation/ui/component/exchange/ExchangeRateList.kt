package com.d108.moyeo.presentation.ui.component.exchange

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography

// 1. 환율 데이터 클래스
data class ExchangeRateData(
    val countryFlag: String,
    val bankName: String,
    val rate: String,
    val change: String,
    val isIncreased: Boolean
)

// 2. 환율 리스트 컴포넌트 (회색 박스)
@Composable
fun ExchangeRateList(
    rates: List<ExchangeRateData>, // 환율 데이터 리스트
    onItemClick: (ExchangeRateData) -> Unit, // 항목 클릭시 실행할 동작
    modifier: Modifier = Modifier // 외부에서 레이아웃 수정시 사용
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        LazyColumn( // 스크롤 가능한 리스트
            verticalArrangement = Arrangement.spacedBy(16.dp) // 항목간 간격 16dp
        ) {
            items(rates) { rate ->
                ExchangeRateItem(
                    rate = rate,
                    onClick = { onItemClick(rate) }
                )
            }
        }
    }
}

// 3. 개별 아이템 (private - 이 파일에서만 사용)
@Composable
private fun ExchangeRateItem(
    rate: ExchangeRateData,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 국기 박스
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color.Gray,
                    shape = RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = rate.countryFlag)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 은행명과 환율
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = rate.bankName,
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = rate.rate,
                style = Typography.bodySmall,
                color = Color.Gray
            )
        }

        // 변동률
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "▲ ${rate.change}",
                style = Typography.bodySmall,
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 화살표
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

// 4. 샘플 데이터 - 테스트용 더미 데이터
object SampleData {
    val rates = listOf(
        ExchangeRateData("🇯🇵", "보스니아 헤르체고비나", "927 JPY = 1,000 KRW", "6.15 (+0.59%)", true),
        ExchangeRateData("🇺🇸", "미국 달러", "1,340 USD = 1,000 KRW", "15.20 (+1.15%)", true),
        ExchangeRateData("🇪🇺", "유럽 유로", "1,450 EUR = 1,000 KRW", "8.30 (-0.58%)", false),
        ExchangeRateData("🇨🇳", "중국 위안", "185 CNY = 1,000 KRW", "2.10 (+0.23%)", true),
        ExchangeRateData("🇬🇧", "영국 파운드", "1,650 GBP = 1,000 KRW", "12.80 (-0.78%)", false)
    )
}