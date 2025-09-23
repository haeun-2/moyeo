package com.d108.moyeo.presentation.ui.screen.home.box

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.core.BoxStore
import com.d108.moyeo.domain.usecase.box.CreateInviteLinkUseCase
import com.d108.moyeo.domain.usecase.history.GetTransactionHistoryUseCase
import com.d108.moyeo.presentation.ui.component.home.Currency
import com.d108.moyeo.presentation.ui.component.home.FilterOptionData.allScopeOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject


sealed class MyBoxNavigationEvent {

    // 모으기
    data class NavigateToCollect(val boxId: String, val currencyCode: String = "KRW") : MyBoxNavigationEvent()

    // 정산하기
    data class NavigateToCalculate(val boxId: Long) : MyBoxNavigationEvent()

    // 초대 링크
    data class InviteLinkReady(val link: String): MyBoxNavigationEvent()

    // Toast 출력
    data class ShowToast(val message: String) : MyBoxNavigationEvent()

    // 박스 멤버 확인
    data class NavigateToMember(val boxId: Long) : MyBoxNavigationEvent()
}

@HiltViewModel
class MyBoxViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase,
    private val boxStore: BoxStore,
    private val createInviteLinkUseCase: CreateInviteLinkUseCase,
): ViewModel() {

    private val TAG = "MyBoxViewModel"
    private val _uiState = MutableStateFlow(MyBoxUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<MyBoxNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()
    val boxId = savedStateHandle.get<Long>("boxId") ?: -1L

    private var searchJob: Job? = null

    init {
//        Log.d(TAG, "${savedStateHandle.keys()}")
//        Log.d(TAG, "boxId: $boxId ")
        val currencyCode = savedStateHandle.get<String>("currencyCode") ?: "KRW"

        viewModelScope.launch {
            val allBoxes = boxStore.boxUiStates.firstOrNull() ?: emptyList()
            Log.d(TAG, "size: ${allBoxes.size}")
            val boxInfo = allBoxes.find { it.id == boxId }
            Log.d(TAG, "boxInfo: $boxInfo")  //


            // TODO: 퍼스널 커런시가 뭐지
            // BoxStore의 personalCurrencies는 CurrencyData 타입이므로 UI에 맞는 Currency 타입으로 변환
            // TODO: 사실상 CurrencyData와 Currency는 같은 모양임...
            val currencies = boxStore.personalCurrencies.value.map { Currency(it.code, it.name) }

            _uiState.update {
                it.copy(
                    boxInfo = boxInfo,
                    selectedCurrencyCode = currencyCode,
                    currencies = currencies
                )
            }

            if (boxId != -1L) {
                loadHistories(boxId = boxId, isInitialLoad = true)
            }
        }
    }

    /**
     * 거래 내역을 불러오는 핵심 함수. 첫 페이지 로드, 다음 페이지 로드, 필터 변경 시 모두 사용
     * @param isInitialLoad true이면 기존 목록을 지우고 0페이지부터, false이면 다음 페이지를 불러와 추가.
     */
    fun loadHistories(boxId: Long, isInitialLoad: Boolean): Job {
        val currentState = _uiState.value
        val pageToLoad = if (isInitialLoad) 0 else currentState.page

        // 이미 로딩 중이거나, 다음 페이지가 없으면(마지막 페이지) 함수를 종료하여 중복 호출을 방지
        if (currentState.isLoading || (!currentState.hasNext && !isInitialLoad)) {
            return viewModelScope.launch {}
        }

        _uiState.update { it.copy(isLoading = true) }

        val filters = currentState.filters
        val calendar = Calendar.getInstance()
        val endDate = calendar.time.toApiDateString()
        val startDate = when (filters.period) {
            Period.THREE_MONTHS -> { calendar.add(Calendar.MONTH, -3); calendar.time.toApiDateString() }
            Period.SIX_MONTHS -> { calendar.add(Calendar.MONTH, -6); calendar.time.toApiDateString() }
            Period.CUSTOM -> { /* TODO */ "" }
            else -> { calendar.add(Calendar.MONTH, -1); calendar.time.toApiDateString() }
        }
        val categoryId = allScopeOptions.indexOf(filters.scope).takeIf { it > 0 }?.toLong()
        val sortDir = filters.sort.name

        return viewModelScope.launch {
            getTransactionHistoryUseCase(
                boxId = boxId,
                currency = currentState.selectedCurrencyCode,
                page = pageToLoad,
                startDate = startDate,
                endDate = endDate,
                keyword = currentState.searchQuery,
                type = "",
                categoryId = categoryId,
                sortDir = sortDir
            ).onSuccess { paginatedHistory ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        // 첫 페이지 로드이면 리스트를 교체하고, 아니면 기존 리스트에 새 리스트를 추가
                        transactions =
                            if (isInitialLoad)
                                paginatedHistory.content
                            else
                                it.transactions + paginatedHistory.content,
                        page = paginatedHistory.page + 1, // 다음 요청할 페이지 번호
                        hasNext = paginatedHistory.hasNext // 다음 페이지 존재 여부
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "거래 내역을 불러오지 못했습니다.") }
            }
        }
    }

    fun loadNextPage() {
        Log.d(TAG, "무한스크롤 시 boxId: $boxId")
        loadHistories(boxId = boxId, isInitialLoad = false)
    }

    // 검색어와 관련된 로직
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        // 이전 검색 요청이 있으면 취소
        searchJob?.cancel()

        // 0.5초 뒤에 검색 실행 (타이핑 할 때마다 API 호출하는 문제 방지)
        searchJob = viewModelScope.launch {
            delay(500L)
            search()
        }
    }

    // 검색 버튼을 클릭했을 때 호출
    fun onSearchClick() {
        search()
    }

    private fun search() {
        // 목록과 페이징을 초기화하고, 새로운 검색어로 0페이지부터 다시 검색
        _uiState.update {
            it.copy(
                transactions = emptyList(),
                page = 0,
                hasNext = true
            )
        }
        loadHistories(boxId = boxId, isInitialLoad = true)
    }

    fun searchWithQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        search() // 기존의 private search 함수 재활용
    }

    fun searchWithCategory(category: String) {
        val newFilters = uiState.value.filters.copy(scope = category)
        onFilterConfirm(newFilters) // 기존의 필터 확인 함수 재활용
    }


    suspend fun forceRefresh() {
        Log.d(TAG, "Lifecycle Event: ON_RESUME. 강제 새로고침을 시작합니다.")
        loadHistories(boxId = boxId, isInitialLoad = true).join()
        Log.d(TAG, "forceRefresh 완료. 다음 작업으로 넘어갑니다.")
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
        val newCurrencyCode = currency?.code ?: "" // '전체 보기'는 빈 문자열 넘겨주면 됨
        Log.d(TAG, "화폐 확정 시 boxId: $boxId")

        // 상태를 업데이트하고, 목록을 비운 뒤, 0페이지부터 다시 검색합니다.
        _uiState.update {
            it.copy(
                selectedCurrencyCode = newCurrencyCode,
                transactions = emptyList(),
                page = 0,
                hasNext = true,
                showCurrencySheet = false
            )
        }
        loadHistories(boxId = boxId, isInitialLoad = true)
    }

    // 필터링 버튼을 클릭했을 때 호출
    fun onFilterClick() {
        _uiState.update { it.copy(showFilterSheet = true) }
    }

    fun onFilterSheetDismiss() {
        _uiState.update { it.copy(showFilterSheet = false) }
    }

    fun onFilterConfirm(newFilters: BoxFilterOptions) {
        _uiState.update {
            it.copy(
                filters = newFilters,
                showFilterSheet = false,
                transactions = emptyList(),
                page = 0,
                hasNext = true
            )
        }
        loadHistories(boxId = boxId, isInitialLoad = true)
    }



    fun onCollectClick() {
        viewModelScope.launch {
            val id = boxId
            val currency = uiState.value.selectedCurrencyCode.ifBlank { "KRW" }
            _navigationEvent.emit(
                MyBoxNavigationEvent.NavigateToCollect(
                    boxId = id.toString(),
                    currencyCode = currency
                )
            )
        }
    }

    fun onCalculateClick() {
        viewModelScope.launch {
            _navigationEvent.emit(MyBoxNavigationEvent.NavigateToCalculate(boxId = boxId))
        }
    }

    fun onExchangeClick() {
        viewModelScope.launch {

        }
    }

    fun onInviteClick() {
        viewModelScope.launch {
            createInviteLinkUseCase(boxId)
                .onSuccess { result ->
                    _navigationEvent.emit(MyBoxNavigationEvent.InviteLinkReady(result.inviteLink))
                }
                .onFailure {
                    _navigationEvent.emit(MyBoxNavigationEvent.ShowToast("초대 링크 생성에 실패했어요."))
                }
        }
    }

    fun onMemberClick() {
        viewModelScope.launch {
            _navigationEvent.emit(MyBoxNavigationEvent.NavigateToMember(boxId))
        }
    }

    private fun Date.toApiDateString(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)
        return formatter.format(this)
    }
}