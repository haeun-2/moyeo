package com.d108.moyeo.presentation.ui.screen.more.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography


// TODO: 변경 완료 시 새로고침 로직 추가

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectedAccountSettingsScreen(
    navController: NavController,
    viewModel: ConnectedAccountViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("연결 계좌 관리", style = Typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        }
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
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(Spacing.Medium)
                ) {
                    uiState.account?.let { account ->
                        AccountCard(
                            bankName = account.bankName,
                            accountNumber = account.bankAccount,
                            onChangeClick = { navController.navigate("account/change") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountCard(
    bankName: String,
    accountNumber: String,
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
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                .padding(Spacing.Medium),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 상단: 은행 로고 자리 + 은행명
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 실제 로고 리소스가 정해지기 전까지 플레이스홀더
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD54F)), // KB 느낌의 임시 색상
                    contentAlignment = Alignment.Center
                ) {
                    Text("KB", style = Typography.labelSmall, color = Color(0xFF5D4037))
                }
                Text(
                    text = bankName,
                    style = Typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 가운데: 계좌번호
            Text(
                text = accountNumber,
                style = Typography.titleLarge
            )

            // 하단: 변경하기 버튼 (현재는 동작 미연결)
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
