package com.d108.moyeo.presentation.ui.screen.qr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.common.GridMoyeoBoxesItem

@Composable
fun QRBoxesScreen(navController: NavController) {
    // 임시 데이터
    val moyeoBoxes = remember {
        List(10) { "모여박스 ${it + 1}" }
    }

    Column(
        modifier = Modifier.padding(
            start = Spacing.Medium, end = Spacing.Medium,
            top = Padding.ScreenTop, bottom = Padding.ScreenBottom
        ),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center // 중앙 정렬
        ) {
            Text(
                text = "내 모여 박스 목록",
                style = Typography.titleLarge
            )
        }

        // 약간의 스페이서
        Spacer(modifier = Modifier.height(Spacing.Large))

        // 2열 그리드의 레이지 뭐시기
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
        ) {
            // common에 있는 GridMoyeoBoxesItem 을 내용물로 함
            items(moyeoBoxes) { boxName ->
                GridMoyeoBoxesItem(boxName = boxName)
            }
        }

    }
}
