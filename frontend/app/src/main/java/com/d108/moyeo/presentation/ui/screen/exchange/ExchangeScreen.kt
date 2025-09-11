package com.d108.moyeo.presentation.ui.screen.exchange

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.DragScope
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.exchange.ExchangeRateData
import com.d108.moyeo.presentation.ui.component.exchange.ExchangeTypeModal

@Composable
fun ExchangeScreen(navController: NavController) {
    val context = LocalContext.current
    var showModal by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var draggedItem by remember {mutableStateOf<Int?>(null)}

    // 편집 가능한 샘플 데이터
    var ratesList by remember {
        mutableStateOf(listOf(
            ExchangeRateData("🇯🇵", "보스니아 헤르체고비나", "927 JPY = 1,000 KRW", "6.15 (+0.59%)", true),
            ExchangeRateData("🇺🇸", "미국 달러", "1,340 USD = 1,000 KRW", "15.20 (+1.15%)", true),
            ExchangeRateData("🇪🇺", "유럽 유로", "1,450 EUR = 1,000 KRW", "8.30 (-0.58%)", false),
            ExchangeRateData("🇨🇳", "중국 위안", "185 CNY = 1,000 KRW", "2.10 (+0.23%)", true),
            ExchangeRateData("🇬🇧", "영국 파운드", "1,650 GBP = 1,000 KRW", "12.80 (-0.78%)", false)
        ))
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
                Toast.makeText(context, "환전하기 버튼을 눌렀습니다", Toast.LENGTH_SHORT).show()
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
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 환율 리스트 (편집 모드에 따라 다르게 표시)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 남은 공간을 모두 차지하도록
                .background(
                    color = if (isEditMode) Color.Gray.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(ratesList) { index, rate ->
                    if (isEditMode) {
                        EditModeRateItem(
                            rate = rate,
                            onDelete = {
                                ratesList = ratesList.filterIndexed { i, _ -> i != index }
                            },
                            // 드래그로 움직이는 동작
                            onDragStart = { draggedItem = index },
                            onDragEnd = { targetIndex ->
                                if (draggedItem != null && draggedItem != targetIndex) {
                                    val newList = ratesList.toMutableList()
                                    val item = newList.removeAt(draggedItem!!)
                                    newList.add(targetIndex, item)
                                    ratesList = newList
                                }
                                draggedItem = null
                            },
                            isDragging = draggedItem == index
                        )
                    } else {
                        NormalRateItem(
                            rate = rate,
                            onClick = { showModal = true }
                        )
                    }
                }
            }
        }

        // 하단 고정 버튼들
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 추가 버튼
            TextButton(
                onClick = {
                    Toast.makeText(context, "추가 버튼 눌렀습니다", Toast.LENGTH_SHORT).show()
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
                Spacer(modifier = Modifier.width(4.dp))
                Text("추가", color = Color.Gray)
            }

            // 수정 버튼
            TextButton(
                onClick = {
                    isEditMode = !isEditMode
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
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isEditMode) "완료" else "수정")
            }
        }
    }

    // 모달 표시
    if (showModal) {
        ExchangeTypeModal(
            onDismiss = {
                showModal = false
            },
            onChargeSelected = {
                showModal = false
                navController.navigate("exchange_keypad/charge")
            },
            onRefundSelected = {
                showModal = false
                navController.navigate("exchange_keypad/refund")
            }
        )
    }
}

// 일반 모드 아이템
@Composable
private fun NormalRateItem(
    rate: ExchangeRateData,
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
            Text(text = rate.countryFlag)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 은행명과 환율
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = rate.bankName,
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = rate.rate,
                style = Typography.bodySmall,
                color = Color.Gray
            )
        }

        // 변동률
        Column(horizontalAlignment = Alignment.End) {
            val isPositive = rate.change.contains("+")
            val changeColor = if (isPositive) Color.Red else Color.Blue
            val changeSymbol = if (isPositive) "▲" else "▼"
            Text(
                text = "$changeSymbol ${rate.change}",
                style = Typography.bodySmall,
                color = changeColor
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

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
    rate: ExchangeRateData,
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
                            // 드래그가 끝났을 때의 위치를 계산하여 새로운 인덱스 결정
                            // 실제 구현에서는 더 정교한 계산이 필요할 수 있습니다
                            onDragEnd(0) // 임시로 0으로 설정
                        }
                    ) { _, _ ->
                        // 드래그 중 처리
                    }
                }
        )

        Spacer(modifier = Modifier.width(12.dp))

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
            Text(text = rate.countryFlag)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 은행명과 환율
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = rate.bankName,
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = rate.rate,
                style = Typography.bodySmall,
                color = Color.Gray
            )
        }

        // 변동률 (상승/하락에 따라 색상 변경)
        Column(horizontalAlignment = Alignment.End) {
            val isPositive = rate.change.contains("+")
            val changeColor = if (isPositive) Color.Red else Color.Blue
            val changeSymbol = if (isPositive) "▲" else "▼"

            Text(
                text = "$changeSymbol ${rate.change}",
                style = Typography.bodySmall,
                color = changeColor
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

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
}