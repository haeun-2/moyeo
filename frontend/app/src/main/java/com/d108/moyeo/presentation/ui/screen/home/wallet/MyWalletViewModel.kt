package com.d108.moyeo.presentation.ui.screen.home.wallet

import androidx.lifecycle.ViewModel
import com.d108.moyeo.presentation.ui.component.home.mywallet.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


// 임시 데이터 클래스
data class WalletTransaction(
    val id: String,
    val date: String,
    val description: String,  // 모여 박스로 넣은 거면 박스 이름이 표시되고, 이외의 경우에는 가맹점 정보가 표시됨
    val amount: String,
    val balance: String,
    val timestamp: String, // "2025.09.08 13:42" 와 같은 전체 시각
    val category: String   // "식/음료", "교통" 등
)

//필터 옵션을 위한 데이터 클래스
data class WalletFilterOptions(
    val period: String = "1개월",
    val scope: String = "전체",
    val sort: String = "최신"
)

// MyWalletScreen의 UI 상태
data class MyWalletUiState(
    val transactions: List<WalletTransaction> = emptyList(),
    val walletName: String = "내 통장",
    val totalBalance: String = "123,456,789 원",
    val searchQuery: String = "",
    val filters: WalletFilterOptions = WalletFilterOptions(),

    // 화폐 단위 선택을 위한 바텀 시트
    val showCurrencySheet: Boolean = false,
    val currencies: List<Currency> = emptyList(),

    // 필터링을 위한 바텀 시트
    val showFilterSheet: Boolean = false
)

class MyWalletViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MyWalletUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // 임시 데이터 로드
        loadInitialData()
    }

    // 검색어와 관련된 로직
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        // TODO: 검색 쿼리에 따라 거래내역 필터링 로직
    }

    fun onFiltersChanged(newFilters: WalletFilterOptions) {
        _uiState.update { it.copy(filters = newFilters) }
        // TODO: 변경된 필터에 따라 거래내역 다시 불러오기
    }

    private fun loadInitialData() {
        val transactions = List(20) {
            WalletTransaction(
                id = it.toString(),
                date = "09.${String.format("%02d", 10 - it)}",
                description = if (it % 2 == 0) "일본 여행" else "GS25 편의점",
                amount = "- 5,${String.format("%03d", it * 100)} 원",
                balance = "11${5 - it},${String.format("%03d", it * 100)} 원",
                timestamp = "2025.09.${String.format("%02d", 10 - it)} 13:42",
                category = if (it % 2 == 0) "여행" else "식/음료"
            )
        }
        val sampleCurrencies = listOf(
            Currency("KRW", "대한민국 원"),
            Currency("USD", "미국 달러"),
            Currency("JPY", "일본 엔"),
            Currency("EUR", "유럽 유로"),
            Currency("CNY", "중국 위안"),
            Currency("GBP", "영국 파운드"),
            Currency("CAD", "캐나다 달러"),
            Currency("AUD", "호주 달러")
        )
        _uiState.update { it.copy(transactions = transactions, currencies = sampleCurrencies) }
    }


    // 잔액 부분을 클릭했을 때 호출할 화폐 바텀 시트 관련 로직
    fun onBalanceClick() {
        _uiState.update { it.copy(showCurrencySheet = true) }
    }

    // 화폐 바텀 시트가 닫힐 때 호출
    fun onCurrencySheetDismiss() {
        _uiState.update { it.copy(showCurrencySheet = false) }
    }

    // 화폐 바텀 시트에서 확정했을 때 호출
    fun onCurrencySelected(currency: Currency?) {
        val newBalance = if (currency == null) {
            "123,456,789 원" // '전체 보기' 선택 시
        } else {
            // 실제로는 해당 화폐의 잔액을 계산해야 합니다. 여기서는 임시 값.
            when (currency.code) {
                "USD" -> "$ 2,500.00"
                "JPY" -> "¥ 350,000"
                "EUR" -> "€ 2,200.50"
                else -> "123,456,789 원"
            }
        }
        // 잔액을 업데이트하고, 바텀시트를 닫습니다.
        _uiState.update { it.copy(totalBalance = newBalance, showCurrencySheet = false) }
    }


    // 필터링 버튼을 클릭했을 때 호출
    fun onFilterClick() {
        _uiState.update { it.copy(showFilterSheet = true) }
    }

    fun onFilterSheetDismiss() {
        _uiState.update { it.copy(showFilterSheet = false) }
    }

    fun onFilterConfirm(newFilters: WalletFilterOptions) {
        _uiState.update { it.copy(filters = newFilters, showFilterSheet = false) }
        // TODO: 변경된 필터에 따라 거래내역 다시 불러오기
    }

}