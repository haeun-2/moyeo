package com.d108.moyeo.presentation.ui.screen.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.model.stats.CategoryStat
import com.d108.moyeo.domain.usecase.box.GetBoxDetailUseCase
import com.d108.moyeo.domain.usecase.history.GetTransactionHistoryUseCase
import com.d108.moyeo.domain.usecase.stats.GetCategoryStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed class HistoryNavEvent {
    data object NavigateToBoxSelection : HistoryNavEvent()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getBoxDetailUseCase: GetBoxDetailUseCase,
    private val getCategoryStatsUseCase: GetCategoryStatsUseCase,
    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HistoryNavEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val TAG = "HistoryViewModel"


    /*
    UI 관련 로직
     */
    fun onSelectBoxClick() {  // 상단 사용 내역 클릭 시
        viewModelScope.launch {
            _navigationEvent.emit(HistoryNavEvent.NavigateToBoxSelection)
        }
    }

    fun onBoxSelected(boxId: Long) {  // 박스 확정 시
        _uiState.update {
            it.copy(
                selectedBoxId = boxId,  // 박스 아이디를 받고
                selectedBox = null,  // 그 외 정보는 초기화
                selectedToggleIndex = 0,
                hasSelectedDateRange = false,
                startDateMillis = null,
                endDateMillis = null,
                allPeriodStatsMap = emptyMap(),
                dateRangeStatsMap = emptyMap(),
                currencyOptions = emptyList(),
                selectedCurrency = "",
                currentStats = null,
                isLoading = true,
                errorMessage = null
            )
        }

        // 아이디 받고 초기화 완료되면 서버에 요청
        viewModelScope.launch {
            getBoxDetailUseCase(boxId)
                .onSuccess { boxDetail ->
                    _uiState.update { it.copy(selectedBox = boxDetail.box) }
                    loadAllPeriodStats(boxId)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = "박스 정보를 불러오지 못했습니다."
                    ) }
                }
        }
    }

    private fun loadAllPeriodStats(boxId: Long) {
        viewModelScope.launch {
            getCategoryStatsUseCase(boxId, "", "")  // 서버와 약속: 빈 칸 주면 전체 주겠다.
                .onSuccess { statsMap ->
                    val currencyOptions = statsMap.keys.toList()  // 서버가 동적으로 옵션을 내려줌
                    val initialCurrency = currencyOptions.firstOrNull()  // 첫 번째를 기본값으로

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            allPeriodStatsMap = statsMap,
                            currencyOptions = currencyOptions,
                            selectedCurrency = initialCurrency ?: "기록 없음",
                            currentStats = initialCurrency?.let { code -> statsMap[code] }
                        )
                    }
                }.onFailure { error ->
                    Log.e(TAG, "전체 기간 통계 로딩 실패", error)
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = "통계 정보를 불러오지 못했습니다."
                    ) }
                }
        }
    }

    // 특정 기간 조회
    private fun loadDateRangeStats(boxId: Long, startDate: Long?, endDate: Long?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val startDateStr = startDate.toApiDateString("")
            val endDateStr = endDate.toApiDateString("")

            getCategoryStatsUseCase(boxId, startDateStr, endDateStr)
                .onSuccess { statsMap ->
                    val currentSelectedCurrency = _uiState.value.selectedCurrency
                    val newCurrencyOptions = statsMap.keys.toList()  // 날짜를 선택하면서 바뀐 드랍박스 옵션

                    val finalSelectedCurrency = if (statsMap.containsKey(currentSelectedCurrency)) {
                        currentSelectedCurrency // 기존 통화가 새 데이터에 있으면 유지
                    } else {
                        newCurrencyOptions.firstOrNull() ?: "기록 없음" // 없으면 새 데이터 중 첫 번째로, 널이면 기록 없다고 표시
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            dateRangeStatsMap = statsMap,
                            currencyOptions = newCurrencyOptions,
                            selectedCurrency = finalSelectedCurrency,
                            currentStats = statsMap[finalSelectedCurrency]
                        )
                    }
                }.onFailure { error ->
                    Log.e(TAG, "기간별 통계 로딩 실패", error)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "통계 정보를 불러오지 못했습니다."
                        )
                    }
                }
        }
    }

    fun onToggleChanged(index: Int) {  // 단순히 전체/기간 토글만 끄고 켜는 상태 -> 날짜는 유지됨
        _uiState.update { it.copy(selectedToggleIndex = index) }

        val currentState = _uiState.value  // 모든 상태를 받음

        if (index == 0) { // "전체" 탭을 선택한 경우
            _uiState.update {
                // 전체 기간 데이터 캐시에서 통화 목록과 현재 통계를 복원
                val currencyOptions = it.allPeriodStatsMap.keys.toList()
                val selectedCurrency = currencyOptions.firstOrNull() ?: "기록 없음"
                it.copy(
                    currencyOptions = currencyOptions,
                    selectedCurrency = selectedCurrency,
                    currentStats = it.allPeriodStatsMap[selectedCurrency]
                )
            }
        } else { // "일자" 탭을 선택한 경우
            if (currentState.hasSelectedDateRange) {
                // 날짜를 선택한 이력이 있다면 기간별 데이터 캐시에서 복원
                _uiState.update {
                    val currencyOptions = it.dateRangeStatsMap.keys.toList()
                    val selectedCurrency = currencyOptions.firstOrNull() ?: "기록 없음"
                    it.copy(
                        currencyOptions = currencyOptions,
                        selectedCurrency = selectedCurrency,
                        currentStats = it.dateRangeStatsMap[selectedCurrency]
                    )
                }
            } else {
                // 날짜를 선택한 이력이 없다면, 표시할 데이터가 없으므로 비워줌
                _uiState.update {
                    it.copy(
                        currentStats = null,
                        currencyOptions = emptyList(),
                        selectedCurrency = ""
                    )
                }
            }
        }
    }

    fun onCurrencyMenuExpanded(isExpanded: Boolean) {  // 화폐 선택 드롭다운 메뉴를 열거나 닫을 때
        _uiState.update { it.copy(isCurrencyMenuExpanded = isExpanded) }
    }

    fun onCurrencySelected(currency: String) {
        _uiState.update { it.copy(selectedCurrency = currency, isCurrencyMenuExpanded = false) }

        // 현재 모드에 따라 적절한 캐시 데이터 선택
        val currentState = _uiState.value
        val newStats = if (currentState.selectedToggleIndex == 0) { // 전체 모드
            currentState.allPeriodStatsMap[currency]
        } else {  // 일자 모드 (날짜가 선택되지 않았으면 null)
            if (currentState.hasSelectedDateRange) {
                currentState.dateRangeStatsMap[currency]
            } else {
                null
            }
        }

        _uiState.update { it.copy(currentStats = newStats) }
    }

    fun onDateRangePickerClick() {
        if (_uiState.value.selectedToggleIndex == 1) {  // 일자 모드
            _uiState.update { it.copy(showDateRangePicker = true) }
        }
    }

    fun onDateRangePickerDismiss() {
        _uiState.update { it.copy(showDateRangePicker = false) }
    }

    fun onDateRangeSelected(startDate: Long?, endDate: Long?) {  // 날짜 변경
        _uiState.update {
            it.copy(
                startDateMillis = startDate,
                endDateMillis = endDate,
                hasSelectedDateRange = true,
                showDateRangePicker = false
            )
        }

        // 새로운 날짜 범위로 API 요청
        _uiState.value.selectedBoxId?.let { boxId ->
            loadDateRangeStats(boxId, startDate, endDate)
        }
    }

    // 바텀 시트 관련
    fun onHistoryItemClick(stat: CategoryStat) {
        _uiState.update { it.copy(
            selectedCategoryForSheet = stat,
            isSheetLoading = true,
            groupedHistoryTransactions = emptyMap() // 이전 맵을 비워줌
        ) }

        val currentState = _uiState.value
        val boxId = currentState.selectedBoxId ?: return
        val startDate = if (currentState.selectedToggleIndex == 0) "" else currentState.startDateMillis.toApiDateString()
        val endDate = if (currentState.selectedToggleIndex == 0) "" else currentState.endDateMillis.toApiDateString()
        val currency = currentState.selectedCurrency

        // UseCase를 호출하여 데이터 요청
        viewModelScope.launch {
            getTransactionHistoryUseCase(
                boxId = boxId,
                startDate = startDate,
                endDate = endDate,
                currency = currency,
                categoryId = stat.categoryId, // stat 객체에서 카테고리 ID를 가져옴
                type = "WITHDRAW",  // 지출 내역만
                page = 0,
                size = 100 // 일단 100개까지 불러오도록 설정
            ).onSuccess { paginatedHistory ->

                val groupedData = paginatedHistory.content.groupBy { transaction ->
                    transaction.datetime.substring(0, 10) // "2025-09-20"
                }
                Log.d(TAG, "상세 거래내역 서버 응답: $paginatedHistory")
                _uiState.update {
                    it.copy(
                        isSheetLoading = false,
                        groupedHistoryTransactions = groupedData // 2. 그룹화된 Map을 UI 상태에 저장
                    )
                }
            }.onFailure { error ->
                // 4. 실패 시, 로딩을 멈추고 에러 처리
                Log.d(TAG, "onHistoryItemClick: $error")
                _uiState.update { it.copy(isSheetLoading = false, errorMessage = "상세 내역을 불러오지 못했습니다.") }
            }
        }
    }

    fun onBottomSheetDismiss() {
        _uiState.update { it.copy(selectedCategoryForSheet = null) }
    }

    private fun Long?.toApiDateString(default: String = ""): String {  // 서버에서 전체 조회를 하기 위해서는 빈칸으로 달라고 했음.
        return this?.let {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)
            formatter.format(Date(it))
        } ?: default
    }
}