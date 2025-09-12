package com.d108.moyeo.presentation.ui.screen.home.wallet

import android.icu.number.Precision.currency
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.presentation.ui.component.home.FilterOptions
import com.d108.moyeo.presentation.ui.component.home.WalletFilterOptionsAdp
import com.d108.moyeo.presentation.ui.component.home.Currency
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


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

// 변환 확장 함수
fun WalletFilterOptions.toAdapter(): WalletFilterOptionsAdp =
    WalletFilterOptionsAdp(period, scope, sort)

fun FilterOptions.toWallet(): WalletFilterOptions = when (this) {
    is WalletFilterOptionsAdp -> WalletFilterOptions(period, scope, sort)
    else -> error("Wallet 화면에서 처리할 수 없는 FilterOptions 타입: $this")
}

// MyWalletScreen의 UI 상태
data class MyWalletUiState(
    val transactions: List<WalletTransaction> = emptyList(),
    val walletName: String = "내 통장", // TODO:월렛 이름 반영
    val totalBalance: String = "123,456,789 원",
    val searchQuery: String = "",
    val filters: WalletFilterOptions = WalletFilterOptions(),

    // 화폐 단위 선택을 위한 바텀 시트
    val showCurrencySheet: Boolean = false,
    val currencies: List<Currency> = emptyList(),
    // 선택받은 화폐 단위
    var choosenCurrencyCode: String = "KRW",

    // 필터링을 위한 바텀 시트
    val showFilterSheet: Boolean = false
)

sealed class WalletNavigationEvent {
    data class NavigateToSending(val currencyCode: String) : WalletNavigationEvent()
}

class MyWalletViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MyWalletUiState())
    val uiState = _uiState.asStateFlow()

    // 내비게이션 이벤트를 전달할 SharedFlow를 추가
    private val _navigationEvent = MutableSharedFlow<WalletNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

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
        _uiState.update {
            it.copy(
                transactions = transactions,
                currencies = sampleCurrencies
            )
        }
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
        _uiState.update { it.copy(totalBalance = newBalance, showCurrencySheet = false,
            choosenCurrencyCode = currency!!.code) }  // 코드 테스트
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

    // 보내기 버튼 클릭시 호출
    fun onSendingClick() {
        val code = uiState.value.choosenCurrencyCode
        // 이 코드를 가지고 SendingScreen으로 진입해야 함
        viewModelScope.launch {
            _navigationEvent.emit(WalletNavigationEvent.NavigateToSending(code))
        }
    }

}