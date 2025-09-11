package com.d108.moyeo.presentation.ui.screen.home.box

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography

@Composable
fun MyBoxDetailScreen(
    navController: NavController,
    transactionId: String,
    viewModel: MyBoxDetailViewModel = viewModel()
) {
    // ViewModel의 상태를 구독합니다.
    val uiState by viewModel.uiState.collectAsState()
    val transaction = uiState.transaction

    // 화면이 처음 생성될 때, 또는 transactionId가 변경될 때 데이터를 로드합니다.
    LaunchedEffect(key1 = transactionId) {
        viewModel.loadTransactionDetails(transactionId)
    }

    // 데이터가 아직 로드되지 않았으면 로딩 화면을 보여줍니다.
    if (transaction == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.Medium, vertical = Spacing.Large),
        horizontalAlignment = Alignment.CenterHorizontally // 각 요소들은 가운데 정렬
    ) {
        // 클릭해서 들어온 멤버 이름이 맨 위에 있음
        Text(
            text = transaction.description,
            style = Typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(Spacing.Medium))

        // 호리젠탈 디바이더
        HorizontalDivider()

        Spacer(modifier = Modifier.height(Spacing.Large))

        // 먼저 카테고리가 있음. Row겠지 그러면? 이 Row 끝엔 카테고리가 있고 제일 오른쪽 끝엔 에딧 버튼이 있음
        DetailInfoRow(
            label = "카테고리",
            content = {
                Row(
                    modifier = Modifier.clickable { /* TODO: 카테고리 편집 바텀시트 열기 */ },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = uiState.selectedCategory, style = Typography.bodyLarge)
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "카테고리 수정",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        // 거래시각
        DetailInfoRow(label = "거래시각", content = { Text("2025.09.08 13:42", style = Typography.bodyLarge) }) // TODO: 실제 timestamp로 변경
        // 거래 금액: +- 얼마
        DetailInfoRow(label = "거래 금액", content = { Text(transaction.amount, style = Typography.bodyLarge, color = MaterialTheme.colorScheme.primary) })
        // 거래 잔액: 얼마
        DetailInfoRow(label = "거래 잔액", content = { Text(transaction.balance, style = Typography.bodyLarge) })

        Spacer(modifier = Modifier.height(Spacing.Large))

        // 호리젠탈 디바이더
        HorizontalDivider()

        // "{들어온 제목}" 검색하기
        SearchActionRow(text = "\"${transaction.description}\" 입금 내역 검색하기", onClick = { /* TODO */ })
        // 호리젠탈 디바이더
        HorizontalDivider()
        // "{저장된 카테고리}" 검색하기 가장 오른쪽엔 > 아이콘
        SearchActionRow(text = "\"${uiState.selectedCategory}\" 검색하기", onClick = { /* TODO */ })


        // 그냥 여백
        Spacer(modifier = Modifier.weight(1f))

        // 제일 아래쪽엔 확인 버튼
        Button(
            onClick = { navController.popBackStack() }, // 이전 화면으로 돌아가기
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("확인")
        }
    }
}

// 정보 표시를 위한 재사용 가능한 Row Composable
@Composable
private fun DetailInfoRow(
    label: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = Typography.bodyLarge, color = Color.Gray)
        Spacer(modifier = Modifier.weight(1f))
        content()
    }
}

// 검색 액션을 위한 재사용 가능한 Row Composable
@Composable
private fun SearchActionRow(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, style = Typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "이동"
        )
    }
}