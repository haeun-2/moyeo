package com.d108.moyeo.presentation.ui.screen.currency.reservation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.usecase.exchange.ReservationExchangeUseCase
import com.d108.moyeo.core.BoxStore
import com.d108.moyeo.domain.usecase.exchange.GetReservationsByBoxUseCase
import com.d108.moyeo.util.currencyUnitMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val boxStore: BoxStore,
    private val reservationExchangeUseCase: ReservationExchangeUseCase,
    private val getReservationsByBoxUseCase: GetReservationsByBoxUseCase,
) : ViewModel() {

    private val _ui = MutableStateFlow(ReservationUiState())
    val ui: StateFlow<ReservationUiState> = _ui

    val boxes = boxStore.boxUiStates

    fun startFromCurrency(boxId: Long) {
        _ui.update {
            it.copy(
                entry = ReservationEntry.CURRENCY,
                selectedBoxId = boxId,
                step = ReservationStep.Home
            )
        }
    }

    fun selectBox(boxId: Long) {
        _ui.update { it.copy(selectedBoxId = boxId) }
    }

    fun selectCurrency(code: String) {
        val name = boxStore.personalCurrencies.value.find { it.code == code }?.name ?: code
        _ui.update {
            it.copy(
                toCurrencyCode = code,
                toCurrencyName = name,
                // 탭 기본값은 KRW, 외화 탭 클릭 시 코드로 바뀜
                selectedTab = "KRW",
                inputAmount = "0",
                krwValue = 0.0,
                foreignValue = 0.0,
                step = ReservationStep.RateInput
            )
        }
    }

    fun changeTargetRate(input: String) {
        _ui.update { it.copy(targetRate = input) }
    }

    fun setTargetRate(rate: String) {
        _ui.update { it.copy(targetRate = rate, step = ReservationStep.AmountInput) }
    }

    fun changeAmountTab(tab: String) {
        _ui.update { s ->
            val newTab = if (tab.isBlank()) "KRW" else tab
            var input = s.inputAmount

            // 해외통화 탭 & 입력이 0 → 기준단위로 초기화 (예: JPY=100)
            if (newTab == s.toCurrencyCode && input == "0") {
                val baseUnit = currencyUnitMap[s.toCurrencyCode]?.baseUnit ?: 1
                input = baseUnit.toString()
            }

            val (krw, foreign) = recalc(newTab, input, s.targetRate, s.toCurrencyCode)
            s.copy(
                selectedTab = newTab,
                inputAmount = input,
                krwValue = krw,
                foreignValue = foreign
            )
        }
    }

    fun changeAmountInput(input: String) {
        _ui.update { s ->
            val (krw, foreign) = recalc(s.selectedTab, input, s.targetRate, s.toCurrencyCode)
            s.copy(inputAmount = input, krwValue = krw, foreignValue = foreign)
        }
    }

    private fun recalc(
        selectedTab: String,
        input: String,
        rateStr: String,
        toCode: String
    ): Pair<Double, Double> {
        val baseUnit = currencyUnitMap[toCode]?.baseUnit ?: 1
        val krwPerBaseUnit = rateStr.toDoubleOrNull() ?: return 0.0 to 0.0   // 예: 100 JPY = 900 KRW → 900
        val krwPerOne = krwPerBaseUnit / baseUnit.toDouble()                 // 예: 1 JPY = 9 KRW
        val amt = input.toDoubleOrNull() ?: return 0.0 to 0.0

        return if (krwPerOne > 0) {
            if (selectedTab == "KRW") {
                // KRW → 외화
                val foreign = amt / krwPerOne
                amt to foreign
            } else {
                // 외화 → KRW
                val krw = amt * krwPerOne
                krw to amt
            }
        } else 0.0 to 0.0
    }

    fun goPeriodSelection() {
        _ui.update { it.copy(step = ReservationStep.PeriodSelection) }
    }

    // ✅ PeriodSelectionContent: 날짜 변경 콜백
    fun changePeriodStart(date: String) { _ui.update { it.copy(periodStart = date) } }
    fun changePeriodEnd(date: String) { _ui.update { it.copy(periodEnd = date) } }

    fun setPeriodAndSubmit(start: String, end: String) {
        val s = _ui.value
        val boxId = s.selectedBoxId
        val toCurrency = s.toCurrencyCode
        val targetRate = s.targetRate.toDoubleOrNull()

        // 서버 전송용 외화 수량 결정
        val foreignAmount = when (s.selectedTab) {
            "KRW" -> s.foreignValue                       // KRW → 외화 환산값
            else  -> s.inputAmount.toDoubleOrNull() ?: 0.0 // 해외통화 탭 입력값(외화)
        }

        if (boxId <= 0L || toCurrency.isBlank() || targetRate == null || foreignAmount <= 0.0) {
            _ui.update { it.copy(errorMessage = "입력값을 확인해주세요.") }
            return
        }

        // 정수 필요 시 정책에 맞게 처리 (아래는 내림)
        val amountForServer = foreignAmount.toLong()

        _ui.update { it.copy(isLoading = true, periodStart = start, periodEnd = end) }
        viewModelScope.launch {
            reservationExchangeUseCase(
                boxId = boxId,
                fromCurrency = "KRW",
                toCurrency = toCurrency,
                amount = amountForServer,  // ✅ 외화 기준 수량 전송
                targetRate = targetRate,
                expiresAt = end
            ).onSuccess {
                _ui.update { it.copy(isLoading = false, step = ReservationStep.Finished) }
            }.onFailure { e ->
                _ui.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun goPrev() {
        val s = _ui.value
        val prev = when (s.step) {
            ReservationStep.Home -> ReservationStep.Home
            ReservationStep.BoxSelection -> ReservationStep.Home
            ReservationStep.CurrencySelection ->
                if (s.entry == ReservationEntry.CURRENCY) ReservationStep.Home else ReservationStep.BoxSelection
            ReservationStep.RateInput -> ReservationStep.CurrencySelection
            ReservationStep.AmountInput -> ReservationStep.RateInput
            ReservationStep.PeriodSelection -> ReservationStep.AmountInput
            ReservationStep.Finished -> ReservationStep.PeriodSelection
        }
        _ui.update { it.copy(step = prev) }
    }

    // 홈 목록 새로고침 등 필요 시 여기에 추가
    fun refreshReservations() {
        val boxId = _ui.value.selectedBoxId
        if (boxId <= 0) return

        _ui.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getReservationsByBoxUseCase(boxId)
                .onSuccess { list ->
                    _ui.update { it.copy(isLoading = false, reservations = list) }
                }
                .onFailure { e ->
                    _ui.update { it.copy(isLoading = false /*, errorMessage = e.message */) }
                }
        }
    }

    fun goHome() { _ui.update { it.copy(step = ReservationStep.Home) } }
    fun goCurrencySelectionIfBoxSelected() {
        if (_ui.value.selectedBoxId > 0L) _ui.update { it.copy(step = ReservationStep.CurrencySelection) }
    }
    fun goRateInput() { if (_ui.value.toCurrencyCode.isNotBlank()) _ui.update { it.copy(step = ReservationStep.RateInput) } }
    fun goAmountInput() { _ui.update { it.copy(step = ReservationStep.AmountInput) } }
    fun submitReservation() { setPeriodAndSubmit(_ui.value.periodStart, _ui.value.periodEnd) }
}
