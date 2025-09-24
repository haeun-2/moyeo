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

    fun loadReservations(boxId: Long = 1L) { // boxId 파라미터 (기본값 설정)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            exchangeRepository.getExchangeReservations(boxId)
                .onSuccess { reservations ->
                    val completedReservations = reservations.filter { reservation ->
                        reservation.status.uppercase() == "COMPLETED"
                    }

                    _uiState.update {
                        it.copy(
                            reservations = completedReservations,
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
                            errorMessage = exception.message
                        )
                    }
                }
        }
    }

    fun cancelReservation(reservationId: String) {
        viewModelScope.launch {
            exchangeRepository.cancelExchangeReservation(reservationId)
                .onSuccess {
                    loadReservations(_uiState.value.currentBoxId)
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(errorMessage = exception.message)
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}