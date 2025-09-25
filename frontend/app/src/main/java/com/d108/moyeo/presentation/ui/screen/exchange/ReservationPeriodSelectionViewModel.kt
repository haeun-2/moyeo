package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.repository.ExchangeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReservationPeriodUiState(
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
    val isReservationComplete: Boolean = false
)

@HiltViewModel
class ReservationPeriodViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository,
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

        _uiState.update {
            it.copy(
                currencyCode = currencyCode,
                currencyName = currencyName,
                targetRate = targetRate,
                amount = amount
            )
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

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                exchangeRepository.createExchangeReservation(
                    boxId = 1L, // 기본값으로 설정 (필요시 사용자 정보에서 가져오기)
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