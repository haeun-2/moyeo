// MyConsultationDetail.kt

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun MyConsultationDetail(navController: NavController, consultationId: Int) {
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
                text = "문의 내역 상세",
                style = Typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Large))

        // TODO: 전달받은 consultationId를 사용하여 실제 문의 내용을 서버에서 조회해야 합니다.
        Text("문의글 ID: $consultationId")
        Spacer(modifier = Modifier.height(Spacing.Medium))
        Text("제목: 환율 적용 기준이 궁금합니다. (임시)")
        Spacer(modifier = Modifier.height(Spacing.Medium))
        Text("내용: ... (임시 내용) ...")
    }
}