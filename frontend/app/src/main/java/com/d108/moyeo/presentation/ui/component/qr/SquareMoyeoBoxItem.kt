package com.d108.moyeo.presentation.ui.component.qr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun SquareMoyeoBoxItem() {
    // Card를 사용하여 각 아이템에 그림자 효과와 모양을 부여합니다.
    Card(
        modifier = Modifier
            .width(130.dp)
            .fillMaxHeight(), // 높이는 부모(LazyRow)를 꽉 채우도록
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.SmallMedium)
        ) {
            // 상단에 Row가 있어서 아이콘 및 통장 제목이 있음
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite, // 임시 아이콘
                    contentDescription = "모여박스 아이콘",
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "일본 여행", // 임시 제목
                    style = Typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = Spacing.Small)
                )
            }

            // 약간의 스페이서
            Spacer(modifier = Modifier.height(Spacing.Small))

            // 화폐 목록
            LazyColumn(
                modifier = Modifier.weight(0.8f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 스크롤을 테스트하기 위해 아이템 수를 늘립니다.
                items(10) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("통화 ${it + 1}", style = Typography.labelSmall)
                        Text("${15000 * (it + 1)}", style = Typography.labelMedium)
                    }
                }
            }
        }
    }
}