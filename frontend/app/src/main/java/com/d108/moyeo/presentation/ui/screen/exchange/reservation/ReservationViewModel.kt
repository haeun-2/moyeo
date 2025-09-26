package com.d108.moyeo.presentation.ui.screen.exchange.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.core.BoxStore
import com.d108.moyeo.core.BoxStoreUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReservationUiState(
    val step: ReservationStep = ReservationStep.Home,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // Home 리스트 (임시)
    val reservations: List<ReservationListItem> = emptyList(),

    val selectedBoxId: Long = -1L,

    val currencyCode: String = "",
    val currencyName: String = "",

    // 환율 입력
    val inputRate: String = "0",
    val currentRate: String = "",

    // 금액 입력
    val selectedTab: String = "",
    val inputAmount: String = "0",
    val krwValue: Double = 0.0,
    val foreignValue: Double = 0.0,

    // 기간
    val startDate: String = "",
    val endDate: String = "",

    val boxId: Long = -1L
)

@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val boxStore: BoxStore,
) : ViewModel() {

    val boxes: StateFlow<List<BoxStoreUiState>> =
        boxStore.boxUiStates
            .map { it } // 예: 필터가 필요하면 .filter { ... }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _uiState = MutableStateFlow(ReservationUiState())
    val uiState: StateFlow<ReservationUiState> = _uiState

    // ✅ 홈 리스트 새로고침 (임시 데모)
    fun refreshReservations() {
        val cur = _uiState.value
        _uiState.value = cur.copy(isLoading = true)
        viewModelScope.launch {
            // TODO: 실제 API 연동
            val demo = listOf(
                ReservationListItem("1", "JPY", "일본 엔", "9.10", "100000", "2025-12-31"),
                ReservationListItem("2", "USD", "미국 달러", "1300", "250000", "2025-11-30")
            )
            _uiState.value = _uiState.value.copy(isLoading = false, reservations = demo)
        }
    }

    fun setBox(boxId: Long) {
        _uiState.value = _uiState.value.copy(selectedBoxId = boxId, boxId = boxId)
    }

    fun setCurrency(code: String, name: String) {
        _uiState.value = _uiState.value.copy(
            currencyCode = code,
            currencyName = name,
            selectedTab = code
        )
    }

    fun setRateInput(value: String) {
        _uiState.value = _uiState.value.copy(inputRate = value.ifBlank { "0" })
    }

    fun setAmountTab(tab: String) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        recalcConverted()
    }

    fun setAmountInput(next: String) {
        _uiState.value = _uiState.value.copy(inputAmount = next.ifBlank { "0" })
        recalcConverted()
    }

    private fun recalcConverted() {
        val s = _uiState.value
        val rate = s.inputRate.toDoubleOrNull()?.takeIf { it > 0.0 } ?: 0.0
        if (rate == 0.0) {
            _uiState.value = s.copy(krwValue = 0.0, foreignValue = 0.0)
            return
        }
        if (s.selectedTab == s.currencyCode) {
            val foreign = s.inputAmount.toDoubleOrNull() ?: 0.0
            _uiState.value = s.copy(foreignValue = foreign, krwValue = foreign * rate)
        } else {
            val krw = s.inputAmount.toDoubleOrNull() ?: 0.0
            _uiState.value = s.copy(krwValue = krw, foreignValue = krw / rate)
        }
    }

    fun setPeriodStart(date: String) { _uiState.value = _uiState.value.copy(startDate = date) }
    fun setPeriodEnd(date: String) { _uiState.value = _uiState.value.copy(endDate = date) }

    fun toNext() {
        val next = when (_uiState.value.step) {
            ReservationStep.Home             -> ReservationStep.BoxSelection
            ReservationStep.BoxSelection      -> ReservationStep.CurrencySelection
            ReservationStep.CurrencySelection -> ReservationStep.RateInput
            ReservationStep.RateInput         -> ReservationStep.AmountInput
            ReservationStep.AmountInput       -> ReservationStep.PeriodSelection
            ReservationStep.PeriodSelection   -> ReservationStep.Finished
            ReservationStep.Finished     -> ReservationStep.Finished
        }
        _uiState.value = _uiState.value.copy(step = next)
    }

    fun toPrev() {
        val prev = when (_uiState.value.step) {
            ReservationStep.Home             -> ReservationStep.Home
            ReservationStep.BoxSelection      -> ReservationStep.Home
            ReservationStep.CurrencySelection -> ReservationStep.BoxSelection
            ReservationStep.RateInput         -> ReservationStep.CurrencySelection
            ReservationStep.AmountInput       -> ReservationStep.RateInput
            ReservationStep.PeriodSelection   -> ReservationStep.AmountInput
            ReservationStep.Finished     -> ReservationStep.PeriodSelection
        }
        _uiState.value = _uiState.value.copy(step = prev)
    }

    fun createReservation(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val s = _uiState.value
        if (s.selectedBoxId <= 0L || s.currencyCode.isBlank() || s.inputRate == "0" ||
            s.inputAmount == "0" || s.startDate.isBlank() || s.endDate.isBlank()
        ) {
            onError("입력값을 확인해 주세요.")
            return
        }
        viewModelScope.launch {
            _uiState.value = s.copy(isLoading = true, errorMessage = null)
            runCatching {
                // TODO 실제 API 연동
                true
            }.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = it.message)
                onError(it.message ?: "예약 생성에 실패했습니다.")
            }
        }
    }

    fun resetToFirst() {
        _uiState.value = ReservationUiState()
    }
}