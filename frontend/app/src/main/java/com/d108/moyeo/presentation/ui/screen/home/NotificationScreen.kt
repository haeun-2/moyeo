package com.d108.moyeo.presentation.ui.screen.home

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.home.notification.NotificationItem

@Composable
fun NotificationScreen(navController: NavController,
                       viewModel: NotificationViewModel = viewModel()) {

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current


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
                text = "알림",
                style = Typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.padding(top = Spacing.Large))

        // 로딩 중일 때, 알림이 없을 때, 알림이 있을 때를 구분하여 표시
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                // 로딩 중일 때
                CircularProgressIndicator()
            } else if (uiState.notifications.isEmpty()) {
                // 값이 없으면 "아직 알림이 없습니다"를 표시
                Text(
                    text = "아직 알림이 없습니다",
                    style = Typography.bodyLarge,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            } else {
                // 값이 있으면 알림을 하나씩 표시
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        items = uiState.notifications,
                        key = { it.id } // 각 아이템의 고유 키
                    ) { notification ->
                        NotificationItem(
                            notification = notification,
                            onClick = {
                                Toast.makeText(context, "'${notification.title}' 클릭됨", Toast.LENGTH_SHORT).show()
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}