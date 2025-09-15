package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// 통화 정보를 담는 데이터 클래스
data class CurrencyData(
    val code: String, // 통화 코드 (예: USD, EUR)
    val name: String,
    val flag: String
)

// 국가 데이터 클래스
data class CountryData(
    val name: String,
    val currencies: List<CurrencyData>
)

// UI 상태를 담는 데이터 클래스
data class ExchangeAddUiState(
    val searchText: String = "",
    val filteredCountries: List<CountryData> = emptyList(),
    val expandedCountries: Set<String> = emptySet(),
    val allCountries: List<CountryData> = emptyList(),
    val popularCurrencies: List<CurrencyData> = emptyList(),
    val isSearchEmpty: Boolean = true
)

class ExchangeAddViewModel : ViewModel() {

    // 내부에서만 수정 가능한 상태
    private val _uiState = MutableStateFlow(ExchangeAddUiState())
    // 외부에서는 읽기만 가능한 상태
    val uiState = _uiState.asStateFlow()

    // 샘플 국가 데이터
    private val allCountriesData = listOf(
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

    private val popularCurrenciesData = listOf(
        CurrencyData("USD", "달러 USD", "🇺🇸"),
        CurrencyData("JPY", "엔화 JPY", "🇯🇵"),
        CurrencyData("EUR", "유로 EUR", "🇪🇺"),
        CurrencyData("CNY", "위안 CNY", "🇨🇳"),
        CurrencyData("GBP", "파운드 GBP", "🇬🇧"),
        CurrencyData("AUD", "호주달러 AUD", "🇦🇺"),
        CurrencyData("CAD", "캐나다달러 CAD", "🇨🇦")
    )

    init {
        initializeData()
    }

    private fun initializeData() {
        _uiState.update {
            it.copy(
                allCountries = allCountriesData,
                filteredCountries = allCountriesData,
                popularCurrencies = popularCurrenciesData
            )
        }
    }

    fun updateSearchText(text: String) {
        _uiState.update { it.copy(searchText = text) }
        performSearch()
    }

    fun selectPopularCurrency(currency: CurrencyData) {
        _uiState.update { it.copy(searchText = currency.name) }
        performSearch()
    }

    fun toggleCountryExpansion(countryName: String) {
        val currentExpanded = _uiState.value.expandedCountries
        val newExpanded = if (currentExpanded.contains(countryName)) {
            currentExpanded - countryName
        } else {
            currentExpanded + countryName
        }

        _uiState.update { it.copy(expandedCountries = newExpanded) }
    }

    private fun performSearch() {
        val searchText = _uiState.value.searchText.trim()

        if (searchText.isBlank()) {
            _uiState.update {
                it.copy(
                    filteredCountries = allCountriesData,
                    expandedCountries = emptySet(),
                    isSearchEmpty = true
                )
            }
        } else {
            val filteredCountries = allCountriesData.mapNotNull { country ->
                val matchingCurrencies = country.currencies.filter { currency ->
                    currency.name.contains(searchText, ignoreCase = true) ||
                            currency.code.contains(searchText, ignoreCase = true) ||
                            country.name.contains(searchText, ignoreCase = true) ||
                            searchText.split(" ").any { part ->
                                currency.name.contains(part, ignoreCase = true) ||
                                        currency.code.contains(part, ignoreCase = true)
                            }
                }

                if (matchingCurrencies.isNotEmpty()) {
                    country.copy(currencies = matchingCurrencies)
                } else null
            }

            // 검색 결과가 있는 국가들 자동으로 펼치기
            val expandedCountries = filteredCountries.map { it.name }.toSet()

            _uiState.update {
                it.copy(
                    filteredCountries = filteredCountries,
                    expandedCountries = expandedCountries,
                    isSearchEmpty = false
                )
            }
        }
    }

    fun clearSearch() {
        _uiState.update {
            it.copy(
                searchText = "",
                filteredCountries = allCountriesData,
                expandedCountries = emptySet(),
                isSearchEmpty = true
            )
        }
    }

    // 통화 선택 시 호출될 함수 (실제로는 환율 리스트에 추가하는 로직)
    fun selectCurrency(currency: CurrencyData) {
        // TODO: 선택된 통화를 환율 리스트에 추가하는 로직 구현
        // 이후 Repository를 통해 서버에 추가 요청
    }
}