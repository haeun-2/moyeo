package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.Spacing
import com.d108.moyeo.presentation.theme.Padding

@Composable
fun ExchangeAddScreen(
    navController: NavController,
    viewModel: ExchangeAddViewModel = viewModel() // ViewModel 주입
) {
    val uiState by viewModel.uiState.collectAsState() // UI 상태 구독
    val keyboardController = LocalSoftwareKeyboardController.current
    // 화면 전체 UI 구성
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(Padding.Content)
    ) {
        // 상단 헤더 (뒤로가기, 화면 제목)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Color.Black
                )
            }
            Text(
                text = "환율 검색 화면",
                style = Typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Medium))

        // 검색창
        OutlinedTextField(
            value = uiState.searchText,
            onValueChange = { viewModel.updateSearchText(it) },
            placeholder = {
                Text(
                    text = "국가명 또는 통화 코드 검색",
                    color = Color.Gray,
                    style = Typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "검색",
                    tint = Color.Gray
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { keyboardController?.hide() }
            ),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(Spacing.Medium))

        // 인기 통화 태그 목록
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
            contentPadding = PaddingValues(horizontal = Spacing.Small)
        ) {
            items(uiState.popularCurrencies) { currency ->
                PopularCurrencyTag(
                    currency = currency,
                    onClick = { viewModel.selectPopularCurrency(currency) }
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.Medium))

        // 국가별 통화 리스트
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.Small) // 아이템 간 수직 간격
        ) {// 검색 결과가 없고, 검색어가 비어있지 않은 경우
            if (uiState.filteredCountries.isEmpty() && !uiState.isSearchEmpty) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.ExtraLarge),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "검색 결과가 없습니다",
                            style = Typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                items(uiState.filteredCountries) { country ->
                    CountrySection(
                        country = country,
                        isExpanded = uiState.expandedCountries.contains(country.name),
                        onToggleExpanded = { viewModel.toggleCountryExpansion(country.name) },
                        onCurrencyClick = { currency ->
                            viewModel.selectCurrency(currency)
                            // 통화 선택 후 환율 화면으로 이동
                            navController.navigate("exchange") {
                                popUpTo("exchange") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PopularCurrencyTag(
    currency: CurrencyData,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = Color.Gray.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = Spacing.SmallMedium, vertical = Spacing.Small)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currency.flag,
                style = Typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(Spacing.ExtraSmall))
            Text(
                text = currency.name,
                style = Typography.bodySmall,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun CountrySection(
    country: CountryData,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onCurrencyClick: (CurrencyData) -> Unit
) {
    Column {
        // 국가 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpanded() }
                .padding(vertical = Spacing.SmallMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = country.name,
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "접기" else "펼치기",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }

        // 통화 리스트
        if (isExpanded && country.currencies.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(start = Spacing.Medium)
            ) {
                country.currencies.forEach { currency ->
                    CurrencyItem(
                        currency = currency,
                        onClick = { onCurrencyClick(currency) }
                    )
                }
            }
        }

        // 구분선
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Gray.copy(alpha = 0.2f))
        )
    }
}

@Composable
private fun CurrencyItem(
    currency: CurrencyData,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.Small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 국기 아이콘
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.Gray, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currency.flag,
                style = Typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.width(Spacing.SmallMedium))

        // 통화 이름
        Text(
            text = currency.name,
            style = Typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )

        // + 버튼
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.Black, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "추가",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}