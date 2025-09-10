package com.d108.moyeo.presentation.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography

// 임시 데이터 클래스
private data class Transaction(
    val id: String,
    val date: String,
    val description: String,
    val amount: String,
    val balance: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWalletScreen(
    navController: NavController,
    viewModel: MyWalletViewModel = viewModel()
) {
    // 임시 데이터
    val transactions = remember {
        List(20) {
            Transaction(
                id = it.toString(),
                date = "09.${String.format("%02d", 10 - it)}",
                description = if (it % 2 == 0) "일본 여행" else "GS25 편의점",
                amount = "- 5,${String.format("%03d", it * 100)} 원",
                balance = "11${5 - it},${String.format("%03d", it * 100)} 원"
            )
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO: 충전하기 로직 */ },
                icon = { Icon(Icons.Default.Add, "충전하기 아이콘") },
                text = { Text(text = "충전하기") }
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // FAB에 가려지지 않도록 패딩 적용
                .padding(horizontal = Spacing.Medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 정보 카드
            TopWalletInfoCard()

            // 검색 및 필터 바
            SearchAndFilterBar()

            // 거래 내역 목록
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(transactions) { transaction ->
                    TransactionRowItem(transaction = transaction)
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                }
            }
        }
    }
}

// 상단 정보 카드 UI
@Composable
private fun TopWalletInfoCard() {
    Card(  // 상단 마이월렛 카드
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp), // 높이를 200dp로 조정
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.Medium),
            verticalArrangement = Arrangement.SpaceBetween  // 이 속성으로 영역 구분
        ) {
            Text(
                text = "내 통장",  // 여기에는 내 월렛의 이름이 떠야 함.
                style = Typography.titleLarge
                // weight는 Column의 직접적인 자식에게만 적용되므로 여기서는 삭제합니다.
            )

            Text(
                text = "123,456,789 원", // 임시 잔액
                style = Typography.displaySmall,
                fontWeight = FontWeight.Bold,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // 버튼 사이에 간격을 줍니다.
            ) {
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f) // 버튼이 남은 공간을 균등하게 차지하도록
                ) {
                    Text("보내기")
                }

                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f) // 버튼이 남은 공간을 균등하게 차지하도록
                ) {
                    Text("환전하기")
                }
            }
        }
    }
}

// 검색 및 필터 바 UI
@Composable
private fun SearchAndFilterBar() {
    var searchQuery by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.Small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 좌측 끝에는 돋보기 버튼.
        Icon(Icons.Default.Search, contentDescription = "검색 아이콘")
        // 남은 공간에는 검색용 인풋텍스트
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Spacing.Small),
            placeholder = { Text("검색", style = Typography.bodySmall) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { /* TODO: 검색 로직 */ }),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )
        // 우측에는 1개월, 전체, 최신순 등 텍스트 버튼
        TextButton(onClick = { /* TODO: 필터 로직 */ }) {
            Text("최신순", style = Typography.bodySmall)
        }
    }
}

// 거래 내역 한 줄 UI
@Composable
private fun TransactionRowItem(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(transaction.date, style = Typography.bodyMedium, color = Color.Gray)
        Spacer(modifier = Modifier.width(Spacing.Medium))
        Text(
            text = transaction.description,
            style = Typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(transaction.amount, style = Typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(transaction.balance, style = Typography.bodySmall, color = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyWalletScreenPreview() {
    MyWalletScreen(navController = rememberNavController())
}