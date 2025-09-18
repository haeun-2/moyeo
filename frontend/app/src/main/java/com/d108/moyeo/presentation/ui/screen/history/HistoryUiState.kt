package com.d108.moyeo.presentation.ui.screen.history

import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.model.stats.CategoryStat

data class HistoryUiState(
    val selectedBox: Box? = null,
    val selectedBoxId: Long? = null,

    // 전체 일자 토글 버튼
    val selectedToggleIndex: Int = 0,

    // TODO: 화폐 선택 옵션은 서버에서 만들어준다고 했음. 기간 설정하면 그 기간동안 사용된 화폐를 가져와줌.
    val isCurrencyMenuExpanded: Boolean = false,
    val currencyOptions: List<String> = listOf("KRW", "USD", "JPY", "EUR"),
    val selectedCurrency: String = "KRW",

    //
    val historyItems: List<CategoryStat> = emptyList(),

    // 데이트피커 관련
    val showDateRangePicker: Boolean = false,
    val startDateMillis: Long? = null,
    val endDateMillis: Long? = null,

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)