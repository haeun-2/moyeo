package com.d108.moyeo.presentation.ui.screen.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items // lazy.items를 import 합니다.
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.primaryLight
import com.d108.moyeo.presentation.ui.component.qr.SquareMoyeoBoxItem
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
fun QRScreen(
    navController: NavController,
    viewModel: QRScreenViewModel = hiltViewModel()
) {
    // ViewModel의 상태를 구독합니다.
    val uiState by viewModel.uiState.collectAsState()
    val bookmarkedBoxes by viewModel.bookmarkedPaymentBoxes.collectAsState()

    // 선택한 걸 중앙에 두려고
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        val newId = savedStateHandle?.get<Long>("newly_bookmarked_id")
        if (newId != null) {
            viewModel.selectBoxOnReturn(newId)
            savedStateHandle.remove<Long>("newly_bookmarked_id")
        }
    }

    LaunchedEffect(bookmarkedBoxes, uiState.scrollToBoxId) {
        val boxIdToScroll = uiState.scrollToBoxId
        // 스크롤 타겟이 있고, 박스 목록이 비어있지 않을 때만 실행
        if (boxIdToScroll != null && bookmarkedBoxes.isNotEmpty()) {
            val index = bookmarkedBoxes.indexOfFirst { it.id == boxIdToScroll }
            if (index != -1) {
                coroutineScope.launch {
                    lazyListState.animateScrollToItem(index = max(0, index - 1))
                    viewModel.onScrollCompleted() // 스크롤 완료 후 상태 초기화
                }
            }
        }
    }

    // 선택된 박스의 이름을 찾습니다. (없으면 기본 텍스트)
    val selectedBoxName = remember(uiState.selectedBoxId, bookmarkedBoxes) {
        bookmarkedBoxes.find { it.id == uiState.selectedBoxId }?.title ?: "결제할 모여 박스를 선택해주세요"
    }


    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = Spacing.Medium,
                    end = Spacing.Medium
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // QR 영역 + 타이머 + 선택된 박스명 + 박스 그리드
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = Padding.ScreenTop), // 기존 상단 패딩도 유지
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(Spacing.Large))

                // QR 200dp 영역
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        uiState.isLoadingQR -> CircularProgressIndicator()
                        uiState.qrImageBitmap != null -> {
                            Image(
                                bitmap = uiState.qrImageBitmap!!,
                                contentDescription = "QR Code",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        else -> {
                            Text("박스를 선택하여 QR코드를 생성하세요.", textAlign = TextAlign.Center)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.ExtraSmall))

                if (uiState.isTimerRunning) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = uiState.timerText,
                            style = Typography.bodyMedium,
                            color = primaryLight
                        )
                        IconButton(onClick = viewModel::onRefreshQRClick) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "새로고침"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.ExtraLarge))

            Column (
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 현재 선택된 박스명
                Text(
                    text = selectedBoxName,
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(Spacing.ExtraLarge))

                // 북마크된 박스 목록
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
                    verticalAlignment = Alignment.CenterVertically,
                    state = lazyListState
                ) {
                    items(
                        items = bookmarkedBoxes,
                        key = { it.id }
                    ) { box ->
                        SquareMoyeoBoxItem(
                            box = box,
                            isSelected = (uiState.selectedBoxId == box.id),
                            onClick = { viewModel.selectBox(box.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.ExtraLarge))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 하단 고정 버튼 (TransferScreen과 동일한 위치/사이즈 가이드)
            Button(
                onClick = { navController.navigate(AppScreen.QRBoxes.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(bottom = Spacing.Medium),
            ) {
                Text("모여 박스 추가하기")
            }
        }
    }
}