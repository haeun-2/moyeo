package com.d108.moyeo.presentation.ui.screen.history

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.onPrimaryLight
import com.d108.moyeo.presentation.theme.primaryLight

@Composable
fun HistoryScreen(navController: NavController) {

    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(
            start = Padding.HorizontalLarge, end = Padding.HorizontalLarge,
            top = Padding.ScreenTop
        ),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center // 중앙 정렬
        ) {
            Row(
                modifier = Modifier
                    .clickable { // 이 Row에만 클릭 이벤트를 적용합니다.
                        Toast.makeText(context, "사용내역 클릭됨!", Toast.LENGTH_SHORT).show()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "사용내역",
                    style = Typography.titleLarge,
                    modifier = Modifier.padding(top = Spacing.Small)
                )

                Spacer(modifier = Modifier.width(Spacing.Small))

                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "상세보기로 이동"
                )
            }

            Button(
                onClick = { Toast.makeText(context, "지도 보기 클릭됨", Toast.LENGTH_SHORT).show() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryLight,
                    contentColor = onPrimaryLight
                ),
                modifier = Modifier.align(Alignment.CenterEnd) // Box의 오른쪽 끝에 배치합니다.
            ) {
                Text(text = "지도 보기")
            }
        }

        Spacer(modifier = Modifier.padding(top = Spacing.Large))

//        Box(
//            modifier = Modifier.fillMaxWidth(),
//            contentAlignment = Alignment.Center // 기본 정렬을 중앙으로 설정
//        ) {
//
//        }

    }
}

@Preview(showBackground = true) // showBackground = true는 미리보기에 배경을 표시합니다.
@Composable
fun HistoryScreenPreview() {
    val navController = rememberNavController()
    HistoryScreen(navController = navController)
}

