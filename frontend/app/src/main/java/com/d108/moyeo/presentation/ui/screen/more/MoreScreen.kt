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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.d108.moyeo.presentation.navigation.AppScreen
import com.d108.moyeo.presentation.theme.Padding
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.ui.component.more.MoreItem
import com.d108.moyeo.util.BiometricAuthManager


@Composable
fun MoreScreen(
    navController: NavController,
    viewModel: MoreViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val menuItems = listOf(
        "공지사항",
        "생체인증",
        "비밀번호 변경",
        "연결 계좌 관리",
        "자주 묻는 질문",
        "채팅 상담"
    )

    // dataStore 상태 구독
    val biometricEnabled by viewModel.biometricEnabled.collectAsStateWithLifecycle()

    // 기기 생체 인증 가능 여부 계산
    val activity = context as? FragmentActivity
    val biometricAuthManager = remember(activity) { activity?.let { BiometricAuthManager(it) } }
    val canBiometric = remember(biometricAuthManager) {
        biometricAuthManager?.canAuthenticate() ?: false
    }

    // 공통 토글 핸들러
    fun handleBiometricToggle(target: Boolean) {
        if (canBiometric) {
            viewModel.onBiometricToggle(target)
            Toast.makeText(
                context,
                "생체인증이 ${if (target) "활성화" else "비활성화"} 되었습니다.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(context, "이 기기에서는 생체인증을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.padding(
            start = Spacing.Medium, end = Spacing.Medium,
            top = Padding.ScreenTop, bottom = Padding.ScreenBottom
        ),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center // 중앙 정렬
        ) {
            Text(
                text = "설정",
                style = Typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Small))

        LazyColumn(
            Modifier.fillMaxSize()
        ) {
            items(menuItems) { menuItem ->
                // "생체인증" 메뉴일 경우에만 Switch를 추가
                // 동시에 생체인증이 가능한 기기인지 확인
                // 생체인증이 불가능할 경우 스위치 비활성화
                if (menuItem == "생체인증") {
                    MoreItem(
                        onClick = { handleBiometricToggle(!biometricEnabled) },
                        text = menuItem,
                        trailingContent = {
                            Switch(
                                checked = biometricEnabled,
                                onCheckedChange = { checked -> handleBiometricToggle(checked) },
                                enabled = canBiometric
                            )
                        }
                    )
                } else {
                    // 다른 메뉴들은 기존과 동일하게 표시
                    MoreItem(
                        onClick = {
                            when (menuItem) {
                                "공지사항" -> navController.navigate(AppScreen.Notices.route)
                                "비밀번호 변경" -> navController.navigate(AppScreen.ChangePassword.route)
                                "연결 계좌 관리" -> navController.navigate(AppScreen.ConnectedAccountSettings.route)
                                "자주 묻는 질문" -> navController.navigate(AppScreen.FAQ.route)
                                "채팅 상담" -> navController.navigate(AppScreen.ChatConsultation.route)
                            }
                        },
                        text = menuItem
                    )
                }
            }
        }
    }
}