package com.d108.moyeo.presentation.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.model.stats.CategoryStat
import com.d108.moyeo.domain.usecase.box.GetBoxDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HistoryNavEvent {
    data object NavigateToBoxSelection : HistoryNavEvent()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getBoxDetailUseCase: GetBoxDetailUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HistoryNavEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        loadHistoryItems()
    }

    /*
    UI 관련 로직
     */
    fun onSelectBoxClick() {  // 상단 사용 내역 클릭 시
        viewModelScope.launch {
            _navigationEvent.emit(HistoryNavEvent.NavigateToBoxSelection)
        }
    }

    fun onBoxSelected(boxId: Long) {  // 박스 확정 시
        _uiState.update { it.copy(selectedBoxId = boxId) }  // 박스 아이디 받음

        viewModelScope.launch {
            getBoxDetailUseCase(boxId)  // 받은 박스 아이디로 상세 조회
                .onSuccess { boxDetail ->
                    _uiState.update {
                        it.copy(selectedBox = boxDetail.box,)
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = "박스 정보를 불러오지 못했습니다.") }
                }


        }
    }

    fun onToggleChanged(index: Int) {  // 전체 / 일자 토글 버튼
        _uiState.update { it.copy(selectedToggleIndex = index) }
        // TODO: 토글 변경 시 서버에 새로운 데이터 요청
    }

    fun onCurrencyMenuExpanded(isExpanded: Boolean) {  // 화폐 선택 드롭다운 메뉴를 열거나 닫을 때
        _uiState.update { it.copy(isCurrencyMenuExpanded = isExpanded) }
    }

    fun onCurrencySelected(currency: String) {  // 화폐 선택 드롭다운 메뉴에서 화폐 골랐을 때
        _uiState.update { it.copy(selectedCurrency = currency, isCurrencyMenuExpanded = false) }
        // TODO: 화폐 변경 시 서버에 새로운 데이터 요청
    }


    fun onDateRangePickerClick() {
        if (_uiState.value.selectedToggleIndex == 1) {  // 일자 모드
            _uiState.update { it.copy(showDateRangePicker = true) }
        }
    }

    fun onDateRangePickerDismiss() {
        _uiState.update { it.copy(showDateRangePicker = false) }
    }

    fun onDateRangeSelected(startDate: Long?, endDate: Long?) {
        _uiState.update { it.copy(startDateMillis = startDate, endDateMillis = endDate) }
        // TODO: 새로운 날짜 범위로 서버에 통계 데이터를 다시 요청하는 API를 호출해야 합니다.
        // 서버는 이 때 파라미터를 yy-mm-dd 형태로 받음
    }

    private fun loadHistoryItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: 실제로는 UseCase를 통해 서버에서 CategoryStats를 받아와야 합니다.
            val dummyStats = List(20) {
                CategoryStat(
                    category = "식/음료",
                    amount = 50000.0 - (it * 1000),
                    ratio = 45.5 - it
                )
            }
            _uiState.update { it.copy(isLoading = false, historyItems = dummyStats) }
        }
    }


}