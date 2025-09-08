package com.d108.moyeo.presentation.ui.screen.exchange

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.exchange.ExchangeRateList
import com.d108.moyeo.presentation.ui.component.exchange.ExchangeRateData

@Composable
fun ExchangeScreen(navController: NavController) {
    val context = LocalContext.current

    // 샘플 데이터 (ExchangeRateList.kt의 ExchangeRateData 사용)
    val sampleRates = listOf(
        ExchangeRateData("🇯🇵", "보스니아 헤르체고비나", "927 JPY = 1,000 KRW", "6.15 (+0.59%)", true),
        ExchangeRateData("🇯🇵", "보스니아 헤르체고비나", "927 JPY = 1,000 KRW", "6.15 (+0.59%)", true),
        ExchangeRateData("🇯🇵", "보스니아 헤르체고비나", "927 JPY = 1,000 KRW", "6.15 (+0.59%)", true),
        ExchangeRateData("🇯🇵", "보스니아 헤르체고비나", "927 JPY = 1,000 KRW", "6.15 (+0.59%)", true),
        ExchangeRateData("🇯🇵", "보스니아 헤르체고비나", "927 JPY = 1,000 KRW", "6.15 (+0.59%)", true),
        ExchangeRateData("🇯🇵", "보스니아 헤르체고비나", "927 JPY = 1,000 KRW", "6.15 (+0.59%)", true),
        ExchangeRateData("🇯🇵", "보스니아 헤르체고비나", "927 JPY = 1,000 KRW", "6.15 (+0.59%)", true)
    )
    // 세로 레이아웃
    Column(
        modifier = Modifier.padding(
            start = Spacing.Medium, end = Spacing.Medium,
            top = Padding.ScreenTop, bottom = Padding.ScreenBottom
        ),
    ) {
        // 제목
        Box(
            modifier = Modifier.fillMaxWidth(), // 가로 전체 차지
            contentAlignment = Alignment.Center // 중앙 정렬
        ) {
            Text(
                text = "환율",
                style = Typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Small))

        // 예약환전하기 버튼
        Button(
            onClick = {
                Toast.makeText(context, "예약환전하기를 눌렀습니다", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF9864FF)
            ),
            shape = RoundedCornerShape(8.dp) // 모서리 둥글게
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "예약 환전하기",
                    color = Color.White,
                    style = Typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 환율 리스트 컴포넌트 사용
        ExchangeRateList(
            rates = sampleRates,
            onItemClick = { rate ->
                Toast.makeText(context, "${rate.bankName} 클릭됨", Toast.LENGTH_SHORT).show()
            }
        )
    }
}