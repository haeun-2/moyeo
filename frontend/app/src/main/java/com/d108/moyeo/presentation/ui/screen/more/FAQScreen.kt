package com.d108.moyeo.presentation.ui.screen.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.secondaryLight
import com.d108.moyeo.presentation.ui.component.more.FAQItems
import com.d108.moyeo.presentation.ui.component.more.FAQSectionHeader


// 임시 FAQ 데이터 클래스
private data class FaqItem(val title: String, val content: String)


@Composable
fun FAQScreen(navController: NavController) {

    var isSearchFocused by remember { mutableStateOf(false) } // 검색창 포커스 상태를 추적
    var searchQuery by remember { mutableStateOf("") }
    val categories = remember {  // 작은 버튼들
        listOf("환율", "계좌 만들기", "이용문의", "알림", "출금", "수수료", "취소")
    }
    val topFaqs = remember {
        listOf(
            FaqItem("모여박스는 어떻게 만드나요?", "홈 화면의 '+' 버튼을 눌러 새로운 모여박스를 만들 수 있습니다. 친구를 초대하고 함께 돈을 모아보세요."),
            FaqItem("환율 정보는 실시간인가요?", "네, 제공되는 환율 정보는 주요 은행사의 데이터를 기반으로 실시간에 가깝게 업데이트됩니다."),
            FaqItem("수수료는 어떻게 되나요?", "개인 간의 송금 및 출금 수수료는 모두 무료입니다. 단, 해외 통화 환전 시에는 소정의 환전 수수료가 부과될 수 있습니다.")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = Spacing.Medium, end = Spacing.Medium,
                top = Padding.ScreenTop, bottom = Padding.ScreenBottom
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 상단 타이틀 ---
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "자주 묻는 질문",
                style = Typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.height(Spacing.Large))

        // --- 검색창 ---
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .onFocusChanged { focusState ->
                    isSearchFocused = focusState.isFocused
                },

            placeholder = {
                // 포커스가 없을 때만 힌트(placeholder)를 보여줌
                if (!isSearchFocused) {
                    Text("궁금한 것을 빠르게 찾아보세요", style = Typography.bodySmall)
                }
            },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "검색") },
            textStyle = Typography.bodySmall,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(Spacing.Large))

        // --- 카테고리 섹션 ---
        FAQSectionHeader("카테고리")
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 90.dp), // 화면 크기에 따라 열 개수 조절
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                OutlinedButton(
                    onClick = { /* TODO: 카테고리별 페이지로 이동 */ },
                    modifier = Modifier.fillMaxWidth()

                ) {
                    Text(category)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 질문 TOP 섹션 ---
        FAQSectionHeader("질문 TOP")
        LazyColumn {
            items(topFaqs) { faq ->
                // FAQ 아이템
                FAQItems(
                    title = faq.title,
                    content = faq.content
                )
            }
        }
    }
}