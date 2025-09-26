package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import android.widget.Toast
import com.d108.moyeo.data.remote.dto.exchange.ExchangeRateItem
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.exchange.ExchangeTypeModal
import com.d108.moyeo.util.CurrencyUtils.getCurrencyFlag
import com.d108.moyeo.util.CurrencyUtils.getCurrencyName

@Composable
fun ExchangeScreen(
    navController: NavController,
    viewModel: ExchangeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // 에러 메시지 표시
    uiState.errorMessage?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, "API 연결 실패: $message", Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = Spacing.Medium,
                end = Spacing.Medium,
                top = Padding.ScreenTop
            )
    ) {
        // 제목
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "환율",
                style = Typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Small))

        // 예약환전하기 버튼
        Button(
            onClick = {
                navController.navigate("exchange_reservation")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "예약 환전하기",
                    color = Color.White,
                    style = Typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(Spacing.Small))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.Medium))

        // 환율 리스트 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    color = if (uiState.isEditMode) Color.Gray.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = Spacing.Small, vertical = Spacing.SmallMedium)
        ) {
            when {
                uiState.isLoading -> {
                    // 로딩 상태
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(Spacing.Medium))
                            Text(
                                text = "환율 정보를 불러오는 중...",
                                style = Typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }
                uiState.ratesList.isEmpty() && !uiState.isLoading -> {
                    // 빈 상태
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "환율 정보가 없습니다",
                                style = Typography.bodyMedium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(Spacing.Small))
                            TextButton(
                                onClick = { viewModel.refreshExchangeRates() }
                            ) {
                                Text("다시 시도")
                            }
                        }
                    }
                }
                else -> {
                    // 환율 리스트 표시
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
                    ) {
                        itemsIndexed(uiState.ratesList) { index, rate ->
                            if (uiState.isEditMode) {
                                EditModeRateItem(
                                    rate = rate,
                                    onDelete = { viewModel.deleteRate(index) },
                                    onDragStart = { viewModel.startDrag(index) },
                                    onDragEnd = { targetIndex -> viewModel.endDrag(targetIndex) },
                                    isDragging = uiState.draggedItem == index
                                )
                            } else {
                                NormalRateItem(
                                    rate = rate,
                                    onClick = { viewModel.showModal(rate) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 하단 고정 버튼들
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.Medium),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 추가 버튼
            TextButton(
                onClick = {
                    navController.navigate("exchange_add")
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "추가",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(Spacing.ExtraSmall))
                Text("추가", color = Color.Gray)
            }

            // 수정 버튼
            TextButton(
                onClick = {
                    viewModel.toggleEditMode()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "수정",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(Spacing.ExtraSmall))
                Text(if (uiState.isEditMode) "완료" else "수정")
            }
        }
    }

    // 모달 표시
    if (uiState.showModal) {
        ExchangeTypeModal(
            onDismiss = { viewModel.hideModal() },
            onChargeSelected = {
                // ViewModel에 저장된 선택 아이템(selectedRate)을 가져옵니다.
                uiState.selectedItem?.let { rate ->
                    val currencyName = getCurrencyName(rate.currencyCode)
                    // 저장된 정보로 내비게이션 경로를 동적으로 생성합니다.
                    navController.navigate(
                        "exchange_keypad/charge?currencyCode=${rate.currencyCode}&currencyName=${currencyName}"
                    )
                }
                viewModel.hideModal() // 모달을 닫습니다.
            },
            onRefundSelected = {
                // ViewModel에 저장된 선택 아이템(selectedRate)을 가져옵니다.
                uiState.selectedItem?.let { rate ->
                    val currencyName = getCurrencyName(rate.currencyCode)
                    // 저장된 정보로 내비게이션 경로를 동적으로 생성합니다.
                    navController.navigate(
                        "exchange_keypad/refund?currencyCode=${rate.currencyCode}&currencyName=${currencyName}"
                    )
                }
                viewModel.hideModal() // 모달을 닫습니다.
            },

            onHistorySelected = {
                // ViewModel에 저장된 선택 아이템(selectedItem)을 가져옵니다.
                uiState.selectedItem?.let { rate ->
                    val currencyName = getCurrencyName(rate.currencyCode)
                    // 'charge'(매수) 모드를 기준으로 히스토리 화면으로 이동하는 경로를 생성합니다.
                    navController.navigate(
                        "exchange_history/${rate.currencyCode}/$currencyName/charge"              )
                }
                viewModel.hideModal() // 모달을 닫습니다.
            }
        )
    }
}

// 일반 모드 아이템
@Composable
private fun NormalRateItem(
    rate: ExchangeRateItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 국기 박스
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(36.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = getCurrencyFlag(rate.currencyCode))
        }

        Spacer(modifier = Modifier.width(Spacing.SmallMedium))

        //과 환율
        Column(modifier = Modifier.weight(1f)) {
            Text(  // 화폐명
                text = getCurrencyName(rate.currencyCode),
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(  // 원래 환율 TODO: 매수환율을 보여줄지?
                text = rate.originalRate.toString(),  // TODO: 자리 수 확인
                style = Typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.width(Spacing.Small))

        // 화살표
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

// 편집 모드 아이템
@Composable
private fun EditModeRateItem(
    rate: ExchangeRateItem,
    onDelete: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: (Int) -> Unit,
    isDragging: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isDragging) Color.Gray.copy(alpha = 0.7f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(if (isDragging) 4.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 드래그 핸들
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "순서 변경",
            tint = Color.Gray,
            modifier = Modifier
                .size(24.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            onDragStart()
                        },
                        onDragEnd = {
                            onDragEnd(0) // 임시로 0 설정
                        }
                    ) { _, _ ->
                        // 드래그 중 처리
                    }
                }
        )

        Spacer(modifier = Modifier.width(Spacing.SmallMedium))

        // 국기 박스
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = getCurrencyFlag(rate.currencyCode))
        }

        Spacer(modifier = Modifier.width(Spacing.SmallMedium))

            Text(
                text = getCurrencyName(rate.currencyCode),
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = rate.originalRate.toString(), // TODO: 글자수 확인
                style = Typography.bodySmall,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(Spacing.Small))

        // 삭제 버튼
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "삭제",
                tint = Color.Red,
                modifier = Modifier.size(20.dp)
            )
        }
    }
