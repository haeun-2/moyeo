package com.d108.moyeo.presentation.ui.component.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.onSurfaceLight
import com.d108.moyeo.presentation.theme.surfaceLight
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankSelectionBottomSheet(
    banks: List<String>,
    onBankSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val gridState = rememberLazyGridState()

    var isDragging by remember { mutableStateOf(false) }            // 현재 제스처 진행 중
    var startAtTop by remember { mutableStateOf(false) }            // 제스처 시작 시 이미 최상단이었는가
    var lockUntilNextGesture by remember { mutableStateOf(false) }  // 이번 제스처 동안 1회 차단 여부
    var reachedTopThisGesture by remember { mutableStateOf(false) } // 이번 제스처 중 처음으로 top에 도달했는가

    fun isAtTop(): Boolean =
        gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset == 0

    // 제스처 시작/종료 감지
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.isScrollInProgress }
            .distinctUntilChanged()
            .collectLatest { inProgress ->
                if (inProgress) {
                    isDragging = true
                    startAtTop = isAtTop()
                    reachedTopThisGesture = false
                    // 잠금은 제스처 중 "top에 처음 닿는 순간"에 켭니다.
                } else {
                    isDragging = false
                    lockUntilNextGesture = false   // 손 떼면 다음 제스처부터 시트가 반응
                    startAtTop = false
                }
            }
    }

    // 스크롤 중 "최상단에 처음 닿는 순간"을 감지 → 이번 제스처만 잠금
    LaunchedEffect(gridState, isDragging) {
        if (!isDragging) return@LaunchedEffect
        snapshotFlow { isAtTop() }
            .distinctUntilChanged()
            .collectLatest { atTop ->
                if (atTop && !startAtTop && !reachedTopThisGesture) {
                    reachedTopThisGesture = true
                    lockUntilNextGesture = true
                }
            }
    }

    // 같은 제스처 동안 top에 닿은 뒤의 "아래 방향" 델타/플링만 차단
    val connection = remember(gridState) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.Drag) return Offset.Zero
                val dy = available.y
                val atTop = isAtTop()
                return if (lockUntilNextGesture && atTop && dy > 0f) {
                    // 이번 제스처에서는 바텀시트로 델타 전달을 막음
                    Offset(0f, dy)
                } else Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                val atTop = isAtTop()
                return if (lockUntilNextGesture && atTop && available.y > 0f) {
                    // 같은 제스처의 아래 방향 플링도 차단
                    available
                } else Velocity.Zero
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        // 바텀시트 내용 전체를 중앙 정렬하기 위한 Column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f) // 최대 높이를 80%로 설정
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally // 자식(그리드)을 수평 중앙에 배치
        ) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2), // 2열 그리드
                modifier = Modifier
                    .fillMaxWidth()
                    .nestedScroll(connection),
                horizontalArrangement = Arrangement.spacedBy(16.dp), // 아이템 간 가로 간격
                verticalArrangement = Arrangement.spacedBy(16.dp)   // 아이템 간 세로 간격
            ) {
                items(banks) { bank ->
                    Button(
                        onClick = { onBankSelected(bank) },
                        modifier = Modifier
                            .size(width = 160.dp, height = 100.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 4.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = surfaceLight,
                            contentColor = onSurfaceLight
                        )
                    ) {
                        Text(
                            text = bank,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}