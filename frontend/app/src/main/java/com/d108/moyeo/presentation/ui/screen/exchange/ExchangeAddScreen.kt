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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.d108.moyeo.presentation.theme.Typography

// 통화 정보 담는 데이터 클래스
data class CurrencyData(
    val code: String, // 통화 코드 (예: "USD", "EUR")
    val name: String,
    val flag: String
)

// 국가 데이터 클래스
data class CountryData(
    val name: String,
    val currencies: List<CurrencyData>
)

@Composable
fun ExchangeAddScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var filteredCountries by remember { mutableStateOf<List<CountryData>>(emptyList()) }
    var expandedCountries by remember { mutableStateOf<Set<String>>(emptySet()) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // 샘플 국가 데이터
    val allCountries = listOf(
        CountryData("유럽", listOf(
            CurrencyData("EUR", "유럽 EUR", "🇪🇺"),
            CurrencyData("GBP", "영국 GBP", "🇬🇧"),
            CurrencyData("CHF", "스위스 CHF", "🇨🇭"),
            CurrencyData("NOK", "노르웨이 NOK", "🇳🇴")
        )),
        CountryData("아시아", listOf(
            CurrencyData("JPY", "일본 JPY", "🇯🇵"),
            CurrencyData("CNY", "중국 CNY", "🇨🇳"),
            CurrencyData("HKD", "홍콩 HKD", "🇭🇰"),
            CurrencyData("SGD", "싱가포르 SGD", "🇸🇬")
        )),
        CountryData("북미", listOf(
            CurrencyData("USD", "미국 USD", "🇺🇸"),
            CurrencyData("CAD", "캐나다 CAD", "🇨🇦"),
            CurrencyData("MXN", "멕시코 MXN", "🇲🇽")
        )),
        CountryData("중남미", listOf(
            CurrencyData("BRL", "브라질 BRL", "🇧🇷"),
            CurrencyData("ARS", "아르헨티나 ARS", "🇦🇷"),
            CurrencyData("CLP", "칠레 CLP", "🇨🇱")
        )),
        CountryData("오세아니아", listOf(
            CurrencyData("AUD", "호주 AUD", "🇦🇺"),
            CurrencyData("NZD", "뉴질랜드 NZD", "🇳🇿")
        )),
        CountryData("아프리카", listOf(
            CurrencyData("ZAR", "남아프리카 ZAR", "🇿🇦"),
            CurrencyData("EGP", "이집트 EGP", "🇪🇬"),
            CurrencyData("NGN", "나이지리아 NGN", "🇳🇬")
        ))
    )

    // 검색 기능
    fun performSearch() {
        keyboardController?.hide() // 검색 실행시 키보드 숨김
        if (searchText.isBlank()) { // 검색어가 비어있으면 전체 목록 보여줌
            filteredCountries = allCountries
            expandedCountries = emptySet()  // 모든 국가 접힌상태로 초기화
        } else {
            filteredCountries = allCountries.map { country ->
                val matchingCurrencies = country.currencies.filter { currency ->
                    currency.name.contains(searchText, ignoreCase = true) || // 통화 이름으로 검색
                            currency.code.contains(searchText, ignoreCase = true) || // 통화 코드로 검색
                            country.name.contains(searchText, ignoreCase = true) || // 국가 이름으로도 검색
                            searchText.split(" ").any { part ->
                                currency.name.contains(part, ignoreCase = true) ||
                                        currency.code.contains(part, ignoreCase = true)
                            }
                }
                if (matchingCurrencies.isNotEmpty()) {
                    country.copy(currencies = matchingCurrencies)
                } else null
            }.filterNotNull()

            // 검색 결과가 있는 국가들을 자동으로 펼치기
            expandedCountries = filteredCountries.map { it.name }.toSet()
        }
    }

    // 초기 데이터 설정 (화면이 처음 구성되고, 검색어 없을때)
    if (filteredCountries.isEmpty() && searchText.isBlank()) {
        filteredCountries = allCountries
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 상단 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) { // 뒤로가기 클릭시 이전 화면으로 이동
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
        // 헤더와 검색창 사이의 간격
        Spacer(modifier = Modifier.height(16.dp))

        // 검색창
        OutlinedTextField(
            value = searchText, // 현재 검색어 표시
            onValueChange = {// 검색어 변경될 때마다 호출
                searchText = it // 상태 업데이트
                performSearch() // 실시간으로 검색 실행
            },
            placeholder = { // 입력 내용 없을 때 보여줄 안내 문구
                Text(
                    text = "국가명 또는 통화 코드 검색",
                    color = Color.Gray,
                    style = Typography.bodyMedium
                )
            },
            leadingIcon = { // 텍스트 필드 앞에 표시될 아이콘
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "검색",
                    tint = Color.Gray
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    performSearch()
                }
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

        Spacer(modifier = Modifier.height(16.dp))

        // 인기 통화 7개 태그 버튼들
        val popularCurrencies = listOf(
            CurrencyData("USD", "달러 USD", "🇺🇸"),
            CurrencyData("JPY", "엔화 JPY", "🇯🇵"),
            CurrencyData("EUR", "유로 EUR", "🇪🇺"),
            CurrencyData("CNY", "위안 CNY", "🇨🇳"),
            CurrencyData("GBP", "파운드 GBP", "🇬🇧"),
            CurrencyData("AUD", "호주달러 AUD", "🇦🇺"),
            CurrencyData("CAD", "캐나다달러 CAD", "🇨🇦")
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(popularCurrencies) { currency ->
                Box(
                    modifier = Modifier
                        .background(
                            color = Color.Gray.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { // 태그 클릭시
                            searchText = currency.name // 해당 통화 이름으로 검색어 설정
                            performSearch()
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp) // 태그 내부 패딩
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 국기 아이콘
                        Text(
                            text = currency.flag,
                            style = Typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.width(4.dp))

                        // 통화 이름
                        Text(
                            text = currency.name,
                            style = Typography.bodySmall,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // 인기 통화 카테고리와 국가별 리스트 사이 간격
        Spacer(modifier = Modifier.height(16.dp))

        // 국가별 통화 리스트
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredCountries.isEmpty()&& searchText.isNotEmpty()) { // 검색 결과가 없고, 검색어가 있는 경우
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
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
                items(filteredCountries) { country ->
//                  // 각 국가 섹션을 표시하는 별도의 composable 함수 호출
                    CountrySection(
                        country = country,
                        isExpanded = expandedCountries.contains(country.name), // 해당 국가가 펼쳐져 있는지
                        onToggleExpanded = { countryName -> // 국가 헤더 클릭시 호출될 콜백
                            expandedCountries = if (expandedCountries.contains(countryName)) {
                                expandedCountries - countryName // 펼쳐있으면 접음
                            } else {
                                expandedCountries + countryName//접혀 있으면 펼침
                            }
                        },
                        onCurrencyClick = { currency ->
                            // + 버튼 클릭시 해당 통화의 환율 페이지로 이동
                            navController.navigate("exchange") {
                                // 기존 스택을 제거하고 새로 시작
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
private fun CountrySection(
    country: CountryData,
    isExpanded: Boolean,
    onToggleExpanded: (String) -> Unit,
    onCurrencyClick: (CurrencyData) -> Unit
) {
    Column {
        // 국가 헤더 (접기/펼치기 가능)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpanded(country.name) }
                .padding(vertical = 12.dp),
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

        // 통화 리스트 (펼쳐져 있을 때만 표시)
        if (isExpanded && country.currencies.isNotEmpty()) { // 펼쳐져 있고, 통화 목록이 비어있지 않은 경우
            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                country.currencies.forEach { currency -> // 각 통화 정보 표시
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp), // 국기와 이름 사이 간격
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 국기 아이콘
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = Color.Gray,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currency.flag,
                                style = Typography.bodyLarge
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // 통화 이름
                        Text(
                            text = currency.name,
                            style = Typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.weight(1f)
                        )

                        // + 버튼
                        IconButton(
                            onClick = { onCurrencyClick(currency) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = Color.Black,
                                        shape = CircleShape
                                    ),
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