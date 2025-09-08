package com.d108.moyeo.presentation.ui.screen.more

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.more.MoreItem


@Composable
fun MoreScreen(navController: NavController) {

    val context = LocalContext.current
    val menuItems = listOf("생체인증", "연결 계좌 관리", "공지 사항", "비밀번호 변경", "자주 묻는 질문", "채팅 상담")

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
                text = "설정",
                style = Typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Small))

        LazyColumn(
            Modifier.fillMaxSize()
        ) {
            items(menuItems) { menuItem ->
                // "생체인증" 메뉴일 경우에만 Switch를 추가
                if (menuItem == "생체인증") {
                    var isChecked by remember { mutableStateOf(false) }
                    MoreItem(
                        onClick = {
                            isChecked = !isChecked
                            val status = if (isChecked) "활성화" else "비활성화"
                            Toast.makeText(context, "생체인증 $status", Toast.LENGTH_SHORT).show()
                        },
                        text = menuItem,
                        trailingContent = {
                            Switch(
                                checked = isChecked,
                                onCheckedChange = {
                                    isChecked = it
                                    val status = if (it) "활성화" else "비활성화"
                                    Toast.makeText(context, "생체인증 $status", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    )
                } else {
                    // 다른 메뉴들은 기존과 동일하게 표시
                    MoreItem(
                        onClick = {
                            when (menuItem) {
                                "연결 계좌 관리" -> navController.navigate(AppScreen.ConnectedAccountSettings.route)
                                "공지 사항" -> navController.navigate(AppScreen.Notices.route)
                                "비밀번호 변경" -> navController.navigate(AppScreen.ChangePassword.route)
                                "자주 묻는 질문" -> navController.navigate(AppScreen.FAQ.route)
                                "채팅 상담" -> navController.navigate(AppScreen.ChatConsultation.route)
                            }
                        },
                        text = menuItem
                    )
                }
            }
        }
    }
}