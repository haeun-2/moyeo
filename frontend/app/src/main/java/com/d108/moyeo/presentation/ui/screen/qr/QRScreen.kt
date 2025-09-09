package com.d108.moyeo.presentation.ui.screen.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.d108.moyeo.R // QR 코드 이미지 예제를 위해 R을 import합니다.
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun QRScreen(navController: NavController) {
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
            contentAlignment = Alignment.Center // 텍스트를 중앙 정렬
        ) {
            // 뒤로가기 버튼을 왼쪽에 배치
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기"
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.Large))

        // 250x250 dp의 QR 코드가 들어올 영역
        Box(
            modifier = Modifier
                .size(250.dp)
                .background(Color.White), // QR 코드의 흰색 배경
            contentAlignment = Alignment.Center
        ) {
            // TODO: 여기에 실제 생성된 QR 코드 Bitmap 이미지를 표시해야 합니다.
            // 지금은 임시 이미지를 사용합니다.
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // 임시 이미지
                contentDescription = "QR Code"
            )
        }

        // 72dp 스페이서
        Spacer(modifier = Modifier.height(72.dp))

        // 내 모여 박스
        Text(
            text = "내 모여 박스",
            style = Typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(Spacing.Medium))

        // 아래쪽에 오른쪽 레이지로우
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Medium)
        ) {
            // 레이지로우 아이템은 우선 나중에 생각
            items(5) { index ->
                Card(
                    modifier = Modifier.size(120.dp, 80.dp)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("통장 ${index + 1}")
                    }
                }
            }
        }

        // 남은 공간을 모두 차지하여 아래 버튼을 맨 밑으로.
        Spacer(modifier = Modifier.weight(1f))

        // 제일 아래쪽에 내 모여 박스 더 보기 버튼
        OutlinedButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("내 모여 박스 더 보기")
        }
    }
}
