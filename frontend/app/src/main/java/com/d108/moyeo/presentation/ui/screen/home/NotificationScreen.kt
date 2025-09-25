package com.d108.moyeo.presentation.ui.screen.home

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.Medium, vertical = Spacing.Medium),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
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
                        style = Typography.titleLarge,
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0.dp),
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
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
                        // 끝 근처 도달 시 추가 로딩
                        LaunchedEffect(listState) {
                            snapshotFlow {
                                val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                                val total = listState.layoutInfo.totalItemsCount
                                lastVisible != null && total > 0 && lastVisible >= total - 3
                            }.collectLatest { nearEnd ->
                                if (nearEnd && !uiState.isLoadingMore && uiState.hasNext) {
                                    viewModel.loadMore()
                                }
                            }
                        }

                        LazyColumn(
                            modifier = Modifier
                                .padding(horizontal = Spacing.Small)
                                .fillMaxSize(),
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
                                        Toast.makeText(
                                            context,
                                            "'${notification.title}' 클릭됨",
                                            Toast.LENGTH_SHORT
                                        ).show()
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
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
