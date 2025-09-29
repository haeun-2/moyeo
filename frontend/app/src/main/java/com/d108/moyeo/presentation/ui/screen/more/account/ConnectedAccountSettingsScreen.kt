package com.d108.moyeo.presentation.ui.screen.more.account

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectedAccountSettingsScreen(
    navController: NavController,
    viewModel: ConnectedAccountViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsState()

    val currentEntry = remember { navController.currentBackStackEntry }
    val refreshFlow = currentEntry
        ?.savedStateHandle
        ?.getStateFlow("refresh", false)

    val shouldRefresh by refreshFlow?.collectAsState() ?: remember { mutableStateOf(false) }

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            viewModel.loadAccount()
            // 소진
            currentEntry?.savedStateHandle?.set("refresh", false)
        }
    }

    BackHandler { navController.popBackStack() }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.Medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "뒤로가기"
                    )
                }

                Text("연결 계좌 관리", style = Typography.titleLarge)
            }
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "오류 발생: ${uiState.error}")
                }
            }

            uiState.account != null -> {
                val account = uiState.account!!

                // 1) 단일 응답의 Base64 로고 → ImageBitmap 변환(1회 캐시)
                val logoFromAccount: ImageBitmap? = remember(account.bankLogoImg) {
                    if (account.bankLogoImg.isBlank()) null
                    else try {
                        val bytes = Base64.decode(account.bankLogoImg, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                    } catch (_: Throwable) {
                        null
                    }
                }

                // 2) 은행 리스트에서 매칭된 로고(있으면) 폴백
                val logoFromBank: ImageBitmap? = uiState.selectedBank?.logoBitmap

                // 최종 사용 로고
                val finalLogo = logoFromAccount ?: logoFromBank

                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(Spacing.Medium)
                ) {
                    AccountCard(
                        bankName = account.bankName,
                        accountNumber = account.bankAccount,
                        bankLogoBitmap = finalLogo,
                        onChangeClick = { navController.navigate("account/change") }
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountCard(
    bankName: String,
    accountNumber: String,
    bankLogoBitmap: ImageBitmap?,
    onChangeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0))
                .padding(Spacing.Medium),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 은행 로고
                if (bankLogoBitmap != null) {
                    Image(
                        bitmap = bankLogoBitmap,
                        contentDescription = "$bankName 로고",
                        modifier = Modifier.size(28.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // 로고가 없을 때의 플레이스홀더
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD54F)), // 임시
                        contentAlignment = Alignment.Center
                    ) {
                        Text("", style = Typography.labelSmall, color = Color(0xFF5D4037))
                    }
                }

                // 은행 이름
                Text(
                    text = bankName,
                    style = Typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 계좌 번호
            Text(
                text = accountNumber,
                style = Typography.headlineLarge
            )

            // 변경하기
            Button(
                onClick = onChangeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text("변경하기", style = Typography.labelLarge)
            }
        }
    }
}
