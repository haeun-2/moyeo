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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.home.notification.NotificationItem
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel()
) {

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
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "뒤로가기"
                )
            }
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
            when {
                uiState.isLoadingInitial -> {
                    CircularProgressIndicator()
                }
                uiState.notifications.isEmpty() -> {
                    Text(
                        text = "아직 알림이 없습니다",
                        style = Typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    val listState = rememberLazyListState()

                    // 끝 근처 도달 시 추가 로딩
                    LaunchedEffect(listState) {
                        snapshotFlow {
                            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                            val total = listState.layoutInfo.totalItemsCount
                            lastVisible != null && total > 0 && lastVisible >= total - 3 // 끝에서 3개 남았을 때
                        }.collectLatest { nearEnd ->
                            if (nearEnd && !uiState.isLoadingMore && uiState.hasNext) {
                                viewModel.loadMore()
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState
                    ) {
                        items(
                            items = uiState.notifications,
                            key = { it.id }
                        ) { notification ->
                            val isLast = notification == uiState.notifications.lastOrNull()
                            NotificationItem(
                                notification = notification,
                                onClick = {
                                    Toast.makeText(context, "'${notification.title}' 클릭됨", Toast.LENGTH_SHORT).show()
                                },
                                showDivider = !isLast
                            )
                        }

                        // 로딩 푸터
                        if (uiState.isLoadingMore) {
                            item(key = "loading_footer") {
                                Box(
                                    modifier = Modifier
                                        .fillParentMaxWidth()
                                        .padding(vertical = Spacing.Medium),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() }
                            }
                        }
                    }
                }
            }
        }
    }
}