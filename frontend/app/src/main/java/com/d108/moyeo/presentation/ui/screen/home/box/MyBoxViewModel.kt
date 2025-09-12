package com.d108.moyeo.presentation.ui.screen.home.box

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.presentation.ui.component.home.BoxFilterOptionsAdp
import com.d108.moyeo.presentation.ui.component.home.FilterOptions
import com.d108.moyeo.presentation.ui.component.home.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 임시 데이터 클래스. 후에 적절한 위치로 이동해야 함.
data class BoxTransaction(
    val id: String,
    val date: String,
    val description: String,
    val amount: String,
    val balance: String,
    val timestamp: String,
    val category: String
)

//필터 옵션을 위한 데이터 클래스
data class BoxFilterOptions(
    val period: String = "1개월",
    val scope: String = "전체",
    val sort: String = "최신"
)

// 변환 확장 함수
fun BoxFilterOptions.toAdapter(): BoxFilterOptionsAdp =
    BoxFilterOptionsAdp(period, scope, sort)

fun FilterOptions.toBox(): BoxFilterOptions = when (this) {
    is BoxFilterOptionsAdp -> BoxFilterOptions(period, scope, sort)
    else -> error("Box 화면에서 처리할 수 없는 FilterOptions 타입: $this")
}

// MyBoxScreen의 UI 상태를 담는 데이터 클래스
data class MyBoxUiState(
    val transactions: List<BoxTransaction> = emptyList(),  // 거래 내역 리스트
    val boxName: String = "", // 초기값은 비워둠
    val totalAmount: String = "", // 초기값은 비워둠
    val searchQuery: String = "",
    val filters: BoxFilterOptions = BoxFilterOptions(),

    // 화폐 단위 선택을 위한 바텀 시트
    val showCurrencySheet: Boolean = false,
    val currencies: List<Currency> = emptyList(),

    // 필터링을 위한 바텀 시트
    val showFilterSheet: Boolean = false
)


class MyBoxViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MyBoxUiState())
    val uiState = _uiState.asStateFlow()


    // 화면이 생성될 때 boxId를 받아와 상세 정보를 로드.
    fun loadBoxDetails(boxId: String) {
        viewModelScope.launch {
            // TODO: 실제 앱에서는 이 boxId를 사용하여 Repository를 통해 서버/DB에서 데이터를 조회해야 합니다.
            val boxData = findBoxDataById(boxId)  // 해당 박스 아이디에 해당하는 것을 찾음
            _uiState.update {
                it.copy(
                    boxName = boxData.boxName,
                    totalAmount = boxData.totalAmount,
                    transactions = boxData.transactions,
                    currencies = boxData.currencies // 화폐 목록도 함께 로드
                )
            }
        }
    }

    // 임시 데이터를 생성하고 반환하는 가상 함수
    private fun findBoxDataById(boxId: String): MyBoxUiState {
        // boxId에 따라 다른 데이터를 보여주는 것처럼 흉내
        val transactions = List(15) {
            BoxTransaction(
                id = it.toString(),
                date = "09.${String.format("%02d", 15 - it)}",
                description = if (it % 3 == 0) "김상훈" else if (it % 3 == 1) "이풍헌" else "박동찬",
                amount = "+ 50,${String.format("%03d", it * 100)} JPY",
                balance = "11${5 - it},${String.format("%03d", it * 100)} JPY",
                timestamp = "2025.09.${String.format("%02d", 10 - it)} 13:42",
                category = if (it % 2 == 0) "여행" else "식/음료"
            )
        }

        // MyWalletViewModel과 같이 화폐 목록도 함께 생성
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
        return MyBoxUiState(
            boxName = "상훈 풍헌 동찬 일본 여행 (ID: $boxId)", // ID를 표시하여 확인
            totalAmount = "50,000 JPY",
            transactions = transactions,
            currencies = sampleCurrencies
        )
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        // TODO: 검색 쿼리에 따라 거래내역 필터링 로직
    }

    // 검색바 옆 필터 글자
    fun onFilterChanged(newFilters: BoxFilterOptions) {
        _uiState.update { it.copy(filters = newFilters) }
        // TODO: 변경된 필터에 따라 거래내역 다시 불러오기
    }


    // 잔액 클릭 시 화폐단위 제시됨
    // 잔액(Amount) 부분을 클릭했을 때 호출
    fun onAmountClick() {
        _uiState.update { it.copy(showCurrencySheet = true) }
    }

    // 화폐 바텀시트가 닫힐 때 호출
    fun onCurrencySheetDismiss() {
        _uiState.update { it.copy(showCurrencySheet = false) }
    }

    // 화폐 바텀시트에서 아이템을 선택했을 때 호출
    fun onCurrencySelected(currency: Currency?) {
        val newAmount = if (currency == null) {
            "50,000 JPY" // '전체 보기' 선택 시
        } else {
            // 실제로는 해당 화폐의 잔액을 계산해야 합니다. 여기서는 임시 값.
            when (currency.code) {
                "USD" -> "$ 450.00"
                "KRW" -> "620,000 원"
                else -> "50,000 JPY"
            }
        }
        _uiState.update { it.copy(totalAmount = newAmount, showCurrencySheet = false) }
    }

    fun onFilterClick() {
        _uiState.update { it.copy(showFilterSheet = true) }
    }

    fun onFilterSheetDismiss() {
        _uiState.update { it.copy(showFilterSheet = false) }
    }

    fun onFilterConfirm(newFilters: BoxFilterOptions) {
        _uiState.update { it.copy(filters = newFilters, showFilterSheet = false) }
        // TODO: 변경된 필터에 따라 거래내역 다시 불러오기
    }

}