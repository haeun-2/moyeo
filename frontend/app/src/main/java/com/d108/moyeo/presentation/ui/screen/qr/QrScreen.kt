package com.d108.moyeo.presentation.ui.screen.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items // lazy.items를 import 합니다.
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.R // QR 코드 이미지 예제를 위해 R을 import합니다.
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.onPrimaryLight
import com.d108.moyeo.presentation.theme.primaryLight
import com.d108.moyeo.presentation.ui.component.qr.SquareMoyeoBoxItem

@Composable
fun QrScreen(
    navController: NavController,
    viewModel: QRScreenViewModel = hiltViewModel()
) {
    // ViewModel의 상태를 구독합니다.
    val uiState by viewModel.uiState.collectAsState()

    // 선택된 박스의 이름을 찾습니다. (없으면 기본 텍스트)
    val selectedBoxName = remember(uiState.selectedBoxId, uiState.bookmarkedBoxes) {
        uiState.bookmarkedBoxes.find { it.id == uiState.selectedBoxId }?.name ?: "결제할 모여 박스를 선택해주세요"
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

        Spacer(modifier = Modifier.height(Spacing.Large))

        // 200 200 dp의 QR 코드가 들어올 영역
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.White), // QR 코드의 흰색 배경
            contentAlignment = Alignment.Center
        ) {
            // TODO: 여기에 실제 생성된 QR 코드 Bitmap 이미지를 표시해야 합니다.
            // 지금은 임시 이미지를 사용.
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // 임시 이미지
                contentDescription = "QR Code"
            )
        }

        Spacer(modifier = Modifier.height(Spacing.ExtraSmall))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1분짜리 타이머. 우선 00:57이라고만 표시
            Text(
                text = "00:57",
                style = Typography.bodyMedium,
                color = primaryLight
            )
            // 그리고 그 옆에는 새로고침 아이콘 버튼이 있음
            IconButton(onClick = { /* TODO: QR 코드 새로고침 로직 */ }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "새로고침"
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.ExtraLarge))

        // 내 모여 박스
        Text(
            text = selectedBoxName,  // 이거 글자가 지금 선택된 통장 글자로 바뀌도록 함
            style = Typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(Spacing.ExtraLarge))

        // 레이지로우가 남은 모든 공간 확보하도록 수정
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // 남은 모든 세로 공간 확보
            horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
            verticalAlignment = Alignment.CenterVertically // 아이템들을 세로 중앙에 정렬
        ) {
            // 레이지로우 아이템은 ViewModel의 리스트를 사용
            items(
                items = uiState.bookmarkedBoxes,
                key = { it.id } // 각 아이템의 고유 키를 지정
            ) { box ->
                SquareMoyeoBoxItem(
                    box = box,
                    isSelected = (uiState.selectedBoxId == box.id),
                    onClick = { viewModel.selectBox(box.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.Medium))

        // 제일 아래쪽에 내 모여 박스 더 보기 버튼
        OutlinedButton(
            onClick = { navController.navigate(AppScreen.QRBoxes.route) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryLight,
                contentColor = onPrimaryLight
            )
        ) {
            Text("내 모여 박스 더 보기")
        }
    }
}