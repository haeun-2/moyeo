package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ReservationPeriodUiState(
    val startDate: String = "",
    val endDate: String = "",
    val showCalendar: Boolean = false,
    val calendarMode: String = "",
    val errorMessage: String? = null
)

@HiltViewModel
class ReservationPeriodViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationPeriodUiState())
    val uiState = _uiState.asStateFlow()

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
}