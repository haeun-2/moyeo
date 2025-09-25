package com.d108.moyeo.presentation.ui.screen.exchange

import android.R.attr.mode
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.core.BoxStore
import com.d108.moyeo.domain.repository.ExchangeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReservationPeriodUiState(
    val boxId: Long = 1L,
    val currencyCode: String = "",
    val currencyName: String = "",
    val targetRate: String = "",
    val amount: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val showCalendar: Boolean = false,
    val calendarMode: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isReservationComplete: Boolean = false,
    val hasInsufficientFunds: Boolean = false
)

@HiltViewModel
class ReservationPeriodViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository,
    private val boxStore: BoxStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationPeriodUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Navigation arguments에서 값들을 가져오기
        val currencyCode = savedStateHandle.get<String>("currencyCode") ?: ""
        val currencyName = savedStateHandle.get<String>("currencyName") ?: ""
        val targetRate = savedStateHandle.get<String>("targetRate") ?: ""
        val amount = savedStateHandle.get<String>("amount") ?: ""
        val boxId = savedStateHandle.get<Long>("boxId") ?: 1L

        _uiState.update {
            it.copy(
                boxId = boxId,
                currencyCode = currencyCode,
                currencyName = currencyName,
                targetRate = targetRate,
                amount = amount
            )
        }
    }

    // initialize 함수 추가
    fun initialize(
        currencyCode: String,
        currencyName: String,
        targetRate: Long,
        amount: String,
        boxId: Long
    ) {
        _uiState.update {
            it.copy(
                boxId = boxId,
                currencyCode = currencyCode,
                currencyName = currencyName,
                targetRate = targetRate.toString(),
                amount = amount
            )
        }

        // 잔액 체크
        checkBalance(boxId, amount.toLongOrNull() ?: 0L)
    }

    // 잔액 체크 함수 추가
    private fun checkBalance(boxId: Long, requiredAmount: Long) {
        viewModelScope.launch {
            val allBoxes = boxStore.boxUiStates.value
            val selectedBox = allBoxes.find { it.id == boxId }

            if (selectedBox != null) {
                val krwBalance = selectedBox.balances.find { it.currency == "KRW" }?.balance ?: 0.0
                val hasEnoughFunds = krwBalance >= requiredAmount

                _uiState.update {
                    it.copy(hasInsufficientFunds = !hasEnoughFunds)
                }

                Log.d(
                    "ReservationPeriod",
                    "BoxId: $boxId, 필요금액: $requiredAmount, 잔액: $krwBalance, 충분한지: $hasEnoughFunds"
                )
            }
        }
    }


    fun showCalendar(mode: String) {
        _uiState.update {
            it.copy(
                showCalendar = true,
                calendarMode = mode
            )
        }
    }

    fun hideCalendar() {
        _uiState.update { it.copy(showCalendar = false) }
    }

    fun selectDate(date: String) {
        if (_uiState.value.calendarMode == "start") {
            _uiState.update { it.copy(startDate = date) }
        } else {
            _uiState.update { it.copy(endDate = date) }
        }
        hideCalendar()
    }

    fun createReservation() {
        val state = _uiState.value

        if (state.startDate.isEmpty() || state.endDate.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "시작일과 종료일을 모두 선택해주세요.")
            }
            return
        }

        // 잔액 체크
        if (state.hasInsufficientFunds) {
            _uiState.update {
                it.copy(errorMessage = "통장에 돈을 충전해주세요")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            Log.d("ReservationPeriod", "=== API 호출 시작 ===")
            Log.d("ReservationPeriod", "선택된 boxId: ${state.boxId}")
            Log.d("ReservationPeriod", "통화: ${state.currencyCode}")
            Log.d("ReservationPeriod", "금액: ${state.amount}")
            Log.d("ReservationPeriod", "목표환율: ${state.targetRate}")

            try {
                exchangeRepository.createExchangeReservation(
                    boxId = state.boxId, // 선택한 boxId 사용
                    fromCurrency = "KRW",
                    toCurrency = state.currencyCode,
                    amount = state.amount.toLong(),
                    targetRate = state.targetRate.toLong(),
                    expiresAt = state.endDate
                ).onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isReservationComplete = true,
                            errorMessage = null
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "예약 생성 실패: ${exception.message}"
                        )
                    }
                }
            } catch (e: NumberFormatException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "입력값이 올바르지 않습니다."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "예상치 못한 오류가 발생했습니다: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}