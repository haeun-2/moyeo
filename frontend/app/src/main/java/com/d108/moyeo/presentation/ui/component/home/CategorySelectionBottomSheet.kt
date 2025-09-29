package com.d108.moyeo.presentation.ui.component.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Typography

// 이 TransactionCategory는 FilterOptionData.kt에 정의된 것을 재사용합니다.
// data class TransactionCategory(val id: Long, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionBottomSheet(
    categories: List<String>,
    onCategorySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.padding(bottom = Spacing.Large)
        ) {
            items(
                items = categories,
            ) { categoryName ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCategorySelected(categoryName) }
                        .padding(Spacing.Large)
                ) {
                    Text(text = categoryName, style = Typography.bodyLarge)
                }
                HorizontalDivider()
            }
        }
    }
}