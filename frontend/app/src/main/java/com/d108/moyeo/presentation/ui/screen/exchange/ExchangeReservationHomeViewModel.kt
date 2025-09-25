package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.data.remote.dto.exchange.ExchangeReservationResponseDto
import com.d108.moyeo.domain.repository.ExchangeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExchangeReservationHomeUiState(
    val reservations: List<ExchangeReservationResponseDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentBoxId: Long = 1L
)

@HiltViewModel
class ExchangeReservationHomeViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExchangeReservationHomeUiState())
    val uiState = _uiState.asStateFlow()

    // 화면이 표시될 때마다 자동으로 데이터를 로드
    init {
        loadReservations()
    }

    fun loadReservations(boxId: Long = 1L) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            exchangeRepository.getExchangeReservations(boxId)
                .onSuccess { reservations ->
                    // 모든 상태의 예약을 표시 (COMPLETED, PENDING 등 모두)
                    _uiState.update {
                        it.copy(
                            reservations = reservations,
                            isLoading = false,
                            errorMessage = null,
                            currentBoxId = boxId
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            reservations = emptyList(),
                            isLoading = false,
                            errorMessage = "예약 목록 조회 실패: ${exception.message}"
                        )
                    }
                }
        }
    }

    // 예약 새로고침 함수 (pull-to-refresh 등에서 사용)
    fun refreshReservations() {
        loadReservations(_uiState.value.currentBoxId)
    }

    fun cancelReservation(reservationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            exchangeRepository.cancelExchangeReservation(reservationId)
                .onSuccess {
                    // 성공 시 목록 새로고침
                    loadReservations(_uiState.value.currentBoxId)
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "예약 취소 실패: ${exception.message}"
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun hideReservation(reservationId: String) {
        _uiState.update { state ->
            state.copy(
                reservations = state.reservations.filterNot { it.id.toString() == reservationId }
            )
        }
    }

}