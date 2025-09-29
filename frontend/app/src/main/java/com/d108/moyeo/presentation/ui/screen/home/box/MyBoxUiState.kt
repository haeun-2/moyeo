package com.d108.moyeo.presentation.ui.screen.home.box

import com.d108.moyeo.core.BoxStoreUiState
import com.d108.moyeo.domain.model.box.BoxMember
import com.d108.moyeo.domain.model.history.HistoryTransaction
import com.d108.moyeo.presentation.ui.component.home.BoxFilterOptionsAdp
import com.d108.moyeo.presentation.ui.component.home.Currency
import com.d108.moyeo.presentation.ui.component.home.FilterOptions

data class MyBoxUiState(
    // --- 데이터 관련 ---
    val boxInfo: BoxStoreUiState? = null, // BoxStore에서 가져올 지갑 정보
    val transactions: List<HistoryTransaction> = emptyList(), // 실제 거래 내역 리스트
    val page: Int = 0,  // 무한 스크롤
    val hasNext: Boolean = true,  // 무한 스크롤
    val members: List<BoxMember> = emptyList(),  // 박스 멤버

    // --- UI 컨트롤 관련 ---
    val searchQuery: String = "",
    val filters: BoxFilterOptions = BoxFilterOptions(),
    val showCurrencySheet: Boolean = false,
    val currencies: List<Currency> = emptyList(),
    val selectedCurrencyCode: String = "KRW",
    val showDateRangePicker: Boolean = false,
    val showFilterSheet: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingNextPage: Boolean = false, // 추가 로딩 상태
    val errorMessage: String? = null
)

//필터 옵션을 위한 데이터 클래스
data class BoxFilterOptions(  // UI 문자열
    val period: Period = Period.ONE_MONTH,
    val scope: String = "전체",
    val sort: SortType = SortType.DESC
)

enum class Period(val displayName: String) {

    // TODO: 직접설정? 전체기간?
    ONE_MONTH("1개월"),
    THREE_MONTHS("3개월"),
    SIX_MONTHS("6개월"),
    CUSTOM("직접 설정")
}

enum class SortType(val displayName: String) {
    DESC("최신"),
    ASC("과거")
}



fun BoxFilterOptions.toAdapter(): BoxFilterOptionsAdp =
    BoxFilterOptionsAdp(
        period = this.period.displayName,
        scope = this.scope,
        sort = this.sort.displayName
    )

// '어댑터 모델' -> '내부 모델'로 변환하는 번역기
fun FilterOptions.toBox(): BoxFilterOptions {
    // 받은 FilterOptions가 Box용 어댑터가 맞는지 확인
    if (this !is BoxFilterOptionsAdp) error("Box 화면에서 처리할 수 없는 필터 타입")

    return BoxFilterOptions(
        period = Period.entries.find { it.displayName == this.period } ?: Period.ONE_MONTH,
        scope = this.scope,
        sort = SortType.entries.find { it.displayName == this.sort } ?: SortType.DESC
    )
}
