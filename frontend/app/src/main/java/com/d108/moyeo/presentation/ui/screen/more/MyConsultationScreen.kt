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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.more.MyConsultationItem

// 임시 데이터 클래스
private data class Consultation(val id: Int, val date: String, val title: String, val isAnswered: Boolean)

@Composable
fun MyConsultationScreen(navController: NavController) {
    val context = LocalContext.current

    // 임시 문의 내역 데이터
    val consultations = remember {
        listOf(
            Consultation(1, "2025.09.07", "환율 적용 기준이 궁금합니다.", true),
            Consultation(2, "2025.09.05", "비밀번호를 잊어버렸습니다.", true),
            Consultation(3, "2025.09.04", "모여박스 인원 제한이 있나요?", false),
            Consultation(4, "2025.09.02", "앱 오류 리포트", true)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = Spacing.Medium, end = Spacing.Medium,
                top = Padding.ScreenTop, bottom = Padding.ScreenBottom
            ),
    ) {
        // --- 상단 타이틀 ---
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "내 문의 내역",
                style = Typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Large))

        // --- 문의 내역 리스트 ---
        LazyColumn {
            items(consultations) { consultation ->
                MyConsultationItem(
                    date = consultation.date,
                    title = consultation.title,
                    isAnswered = consultation.isAnswered,
                    onClick = {
                        // 클릭 시 해당 글을 조회함 (화면 이동)
                         navController.navigate("consultation_detail/${consultation.id}")
                    }
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}