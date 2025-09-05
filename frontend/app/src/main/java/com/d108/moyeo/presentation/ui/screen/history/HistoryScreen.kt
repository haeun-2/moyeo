package com.d108.moyeo.presentation.ui.screen.history

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.surfaceLight
import com.d108.moyeo.presentation.ui.component.history.HistoryDetailBottomSheet
import com.d108.moyeo.presentation.ui.component.history.HistoryItem


@Composable
fun HistoryScreen(navController: NavController) {

    val context = LocalContext.current
    var showHistoryDetailSheet by remember { mutableStateOf(false) }

    if (showHistoryDetailSheet) {
        HistoryDetailBottomSheet(onDismiss = { showHistoryDetailSheet = false })
    }

    Column(
        modifier = Modifier.padding(
            start = Padding.HorizontalLarge, end = Padding.HorizontalLarge,
            top = Padding.ScreenTop, bottom = Padding.ScreenBottom
        ),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center // 중앙 정렬
        ) {
            Row(
                modifier = Modifier
                    .clickable {
                        showHistoryDetailSheet = true
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "사용내역",
                    style = Typography.titleLarge,
                    modifier = Modifier.padding(top = Spacing.Small)
                )

                Spacer(modifier = Modifier.width(Spacing.Small))

                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "상세보기로 이동"
                )
            }
        }

        Spacer(modifier = Modifier.padding(top = Spacing.SmallMedium))  // 사용내역 헤더와 아래 박스 사이의 여백

        // 아래 큰 박스 영역
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    var selectedIndex by remember { mutableStateOf(0) }
                    val options = listOf("전체", "일자")
                    SingleChoiceSegmentedButtonRow {
                        options.forEachIndexed { index, label ->
                            SegmentedButton (
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                                onClick = { selectedIndex = index },
                                selected = index == selectedIndex
                            ) {
                                Text(label)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.SmallMedium))

            // 2. 원형 그래프 (임시 플레이스홀더)
            Box(
                modifier = Modifier
                    .size(214.dp)  // 이 크기를 상수화 할 것!!
                    .clip(CircleShape)
                    .background(surfaceLight),
                contentAlignment = Alignment.Center
            ) {
                Text("원형 그래프")
            }

            Spacer(modifier = Modifier.height(Spacing.Medium))

            // 3. 날짜 표시 영역
            Text(
                text = "2025년 9월 1일 ~ 2025년 9월 5일",
                style = Typography.bodyLarge,
                modifier = Modifier.clickable {
                    Toast.makeText(context, "날짜 텍스트 클릭됨", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(Spacing.Small))

            // 4. 지도보기 글자
            Text(
                text = "지도 보기",
                style = Typography.bodyLarge,
                modifier = Modifier.clickable {
                    Toast.makeText(context, "지도 보기(텍스트) 클릭됨", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(Spacing.Large))

            LazyColumn(
                modifier = Modifier.width(248.dp),  // 이 크기를 상수화 할 것!
                verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
            ) {
                items(20) { index ->
                    HistoryItem()
                }
            }
        }
    }
}

@Preview(showBackground = true) // showBackground = true는 미리보기에 배경을 표시합니다.
@Composable
fun HistoryScreenPreview() {
    val navController = rememberNavController()
    HistoryScreen(navController = navController)
}

