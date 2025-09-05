package com.d108.moyeo.presentation.ui.screen.more

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.primaryLight
import com.d108.moyeo.presentation.ui.component.more.NoticesItem

// 임시 데이터 클래스
private data class Notice(val date: String, val title: String, val content: String)

@Composable
fun NoticesScreen(navController: NavController) {

    // 임시 공지사항 데이터 리스트
    val notices = remember {
        listOf(
            Notice("2025.09.06", "모여 서비스 점검 안내", "보다 나은 서비스 제공을 위해 시스템 점검을 진행할 예정입니다. 양해 부탁드립니다."),
            Notice("2025.09.04", "개인정보처리방침 개정 안내", "개인정보처리방침이 일부 개정되어 안내드립니다. 변경된 내용은 설정 메뉴에서 확인하실 수 있습니다."),
            Notice("2025.09.01", "모여 앱 v1.2 업데이트 안내", "새로운 기능이 추가된 v1.2 업데이트가 출시되었습니다. 지금 바로 업데이트하고 새로운 기능을 만나보세요!"),
            Notice("2025.08.25", "환율 정보 제공 국가 확대 안내", "// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n"),
            Notice("2025.08.25", "환율 정보 제공 국가 확대 안내", "// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n"),
            Notice("2025.08.25", "환율 정보 제공 국가 확대 안내", "// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n"),
            Notice("2025.08.25", "환율 정보 제공 국가 확대 안내", "// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n"),
            Notice("2025.08.25", "환율 정보 제공 국가 확대 안내", "// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n"),
            Notice("2025.08.25", "환율 정보 제공 국가 확대 안내", "// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n"),
            Notice("2025.08.25", "환율 정보 제공 국가 확대 안내", "// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n"),
            Notice("2025.08.25", "환율 정보 제공 국가 확대 안내", "// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n"),
            Notice("2025.08.25", "환율 정보 제공 국가 확대 안내", "// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n"),
            Notice("2025.08.25", "환율 정보 제공 국가 확대 안내", "// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n// 임시 데이터 클래스\n" +
                    "private data class Notice(val date: String, val title: String, val content: String)\n"),

        )
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
                text = "공지사항",
                style = Typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Small))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(notices) { notice ->
                NoticesItem(
                    date = notice.date,
                    title = notice.title,
                    content = notice.content
                )
                HorizontalDivider(color = primaryLight, thickness = 0.5.dp) // 구분선
            }
        }
    }
}