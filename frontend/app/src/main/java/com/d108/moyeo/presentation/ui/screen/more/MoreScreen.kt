package com.d108.moyeo.presentation.ui.screen.more

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.d108.moyeo.presentation.navigation.AppScreen
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
        "자주 묻는 질문",
        "채팅 상담",
        "생체인증",
        "연결 계좌 관리",
        "비밀번호 변경",
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

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.Medium, vertical = Spacing.Medium)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = Spacing.SmallMedium)
                        .sizeIn(minHeight = 48.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "더보기",
                        style = Typography.titleLarge
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = Spacing.Medium,
                    end = Spacing.Medium,
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                itemsIndexed(menuItems) { index, menuItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                when (menuItem) {
                                    "공지사항" -> navController.navigate(AppScreen.Notices.route)
                                    "자주 묻는 질문" -> navController.navigate(AppScreen.FAQ.route)
                                    "채팅 상담" -> navController.navigate(AppScreen.ChatConsultation.route)
                                    "생체인증" -> handleBiometricToggle(!biometricEnabled)
                                    "연결 계좌 관리" -> navController.navigate(AppScreen.ConnectedAccountSettings.route)
                                    "비밀번호 변경" -> navController.navigate(AppScreen.ChangePassword.route)
                                }
                            }
                    ) {
                        MoreItem(
                            text = menuItem,
                            modifier = Modifier.fillMaxWidth(),
                            trailingContent = if (menuItem == "생체인증") {
                                {
                                    Switch(
                                        checked = biometricEnabled,
                                        onCheckedChange = { checked -> handleBiometricToggle(checked) },
                                        enabled = canBiometric
                                    )
                                }
                            } else null
                        )
                    }

                    if (index < menuItems.lastIndex) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}