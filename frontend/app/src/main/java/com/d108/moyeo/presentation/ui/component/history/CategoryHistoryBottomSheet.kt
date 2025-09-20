package com.d108.moyeo.presentation.ui.component.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d108.moyeo.domain.model.history.HistoryTransaction
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 특정 카테고리의 상세 거래 내역을 보여주는 ModalBottomSheet.
 * @param categoryName 표시할 카테고리의 이름.
 * @param period 표시할 기간 문자열 (예: "전체 기간", "2025년 9월 7일 ~ 9월 14일").
 * @param totalAmount 해당 카테고리의 총 사용액.
 * @param currency 통화 단위.
 * @param transactions 해당 카테고리에 속하는 개별 거래 내역 리스트.
 * @param onDismiss 바텀시트가 닫힐 때 호출될 람다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryHistoryBottomSheet( // TODO: 정렬 기준 변경 및 무한스크롤화
    categoryName: String,
    period: String,
    totalAmount: Double,
    currency: String,
    groupedHistoryTransactions: Map<String, List<HistoryTransaction>>,
    onDismiss: () -> Unit
) {
    val formattedTotalAmount = DecimalFormat("#,###.##").format(totalAmount)

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Large)
                .padding(bottom = Spacing.ExtraLarge)
        ) {
            // 최상단에 기간 표시
            Text(
                text = period,
                style = Typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(Spacing.Large))

            // 2. 총 사용액
            Text(text = "${categoryName}의 총 사용액", style = Typography.titleMedium)
            Text(
                text = "$formattedTotalAmount $currency",
                style = Typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(Spacing.Medium))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(Spacing.Medium))

            // 3. 카테고리별 거래 내역 리스트
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp),
                contentPadding = PaddingValues(vertical = Spacing.Medium)
            ) {
                // 1. 그룹화된 Map의 각 엔트리(날짜 + 해당 날짜의 거래 목록)를 순회합니다.
                groupedHistoryTransactions.forEach { (date, transactionsOnThatDate) ->

                    // 2. 날짜를 위한 '헤더' 아이템을 추가합니다.
                    item {
                        DateHeader(date = date)
                    }

                    // 3. 해당 날짜에 속한 '거래 내역' 아이템들을 추가합니다.
                    items(
                        items = transactionsOnThatDate,
                        key = { transaction -> transaction.id }
                    ) { transaction ->
                        TransactionRow(transaction = transaction)
                    }
                }
            }
        }
    }
}

@Composable
private fun DateHeader(date: String) {
    // "2025-09-20" -> "2025.09.20" 형식으로 변경
    val formattedDate = date.replace("-", ".")
    Text(
        text = formattedDate,
        style = Typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.Small)
    )
}

/**
 * 바텀시트의 LazyColumn에 들어갈 개별 거래 내역 아이템.
 */
@Composable
private fun TransactionRow(transaction: HistoryTransaction) {
    val formattedAmount = DecimalFormat("#,###.##").format(transaction.amount)
    val formattedDate = transaction.datetime.toDate()?.let {
        SimpleDateFormat("M월 d일", Locale.KOREAN).format(it)
    } ?: ""

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 설명 (거래처 이름 등)
            Text(
                text = transaction.title,
                style = Typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            // 지출액
            Text(
                text = "$formattedAmount ${transaction.currency}",  // 서버에서 마이너스 붙여줌
                style = Typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
        // 날짜
        Text(
            text = formattedDate,
            style = Typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// String("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") -> Date? 변환 헬퍼
private fun String.toDate(): Date? {
    return try {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(this.take(19))
    } catch (e: Exception) {
        null
    }
}