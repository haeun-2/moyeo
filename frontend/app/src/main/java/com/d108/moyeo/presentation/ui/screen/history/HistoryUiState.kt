package com.d108.moyeo.presentation.ui.screen.history

import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.model.history.HistoryTransaction
import com.d108.moyeo.domain.model.stats.CategoryStat
import com.d108.moyeo.domain.model.stats.CategoryStats

data class HistoryUiState(
    val selectedBox: Box? = null,
    val selectedBoxId: Long? = null,

    // 전체 일자 토글 버튼
    val selectedToggleIndex: Int = 0,

    // 드롭 다운 메뉴
    val isCurrencyMenuExpanded: Boolean = false,
    val currencyOptions: List<String> = emptyList(), // 서버 응답에 따라 동적으로 채워짐
    val selectedCurrency: String = "",

    // 서버 응답 및 캐시된 데이터 관리
    val allPeriodStatsMap: Map<String, CategoryStats> = emptyMap(), // 전체 기간 데이터
    val dateRangeStatsMap: Map<String, CategoryStats> = emptyMap(), // 선택된 기간 데이터
    val currentStats: CategoryStats? = null, // 현재 화면에 표시할 데이터

    // 데이트피커 관련
    val hasSelectedDateRange: Boolean = false, // 사용자가 날짜를 선택했는지
    val showDateRangePicker: Boolean = false,
    val startDateMillis: Long? = null,
    val endDateMillis: Long? = null,

    // 바텀 시트 관련
    val selectedCategoryForSheet: CategoryStat? = null, // 바텀시트에 보여줄 카테고리 정보
    val groupedHistoryTransactions: Map<String, List<HistoryTransaction>> = emptyMap(), // 날짜별로 그룹화된 거래 내역
    val isSheetLoading: Boolean = false, // 바텀시트 내부의 로딩 상태

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)