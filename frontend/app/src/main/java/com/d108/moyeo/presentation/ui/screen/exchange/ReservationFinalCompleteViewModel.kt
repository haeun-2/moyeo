package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.core.BoxStore
import com.d108.moyeo.domain.repository.ExchangeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class ReservationFinalCompleteUiState(
    val boxId: Long = 1L,
    val currencyCode: String = "",
    val targetRate: Long = 0L,
    val amount: Long = 0L,
    val expiresAt: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val hasInsufficientFunds: Boolean = false
)

@HiltViewModel
class ReservationFinalCompleteViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository,
    private val boxStore: BoxStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationFinalCompleteUiState())
    val uiState = _uiState.asStateFlow()

    fun initialize(
        boxId: Long,
        currencyCode: String,
        targetRate: String,
        amount: String,
        endDate: String
    ) {
        val targetRateLong = targetRate.toLongOrNull() ?: 0L
        val amountLong = amount.toLongOrNull() ?: 0L

        _uiState.update {
            it.copy(
                boxId = boxId,
                currencyCode = currencyCode,
                targetRate = targetRateLong,
                amount = amountLong,
                expiresAt = endDate
            )
        }

        // 잔액 체크
        checkBalance(boxId, amountLong)
    }

    private fun checkBalance(boxId: Long, requiredAmount: Long) {
        viewModelScope.launch {
            val allBoxes = boxStore.boxUiStates.value
            val selectedBox = allBoxes.find { it.id == boxId }

            if (selectedBox != null) {
                // KRW 잔액 확인
                val krwBalance = selectedBox.balances.find { it.currency == "KRW" }?.balance ?: 0.0
                val hasEnoughFunds = krwBalance >= requiredAmount

                _uiState.update {
                    it.copy(hasInsufficientFunds = !hasEnoughFunds)
                }

                if (!hasEnoughFunds) {
                    _uiState.update {
                        it.copy(errorMessage = "통장에 돈을 충전해주세요. 현재 잔액: ${krwBalance.toInt()}원, 필요 금액: ${requiredAmount}원")
                    }
                }
            }
        }
    }

    fun createReservation() {
        val currentState = _uiState.value

        // 잔액 부족 시 예약 중단
        if (currentState.hasInsufficientFunds) {
            _uiState.update {
                it.copy(errorMessage = "통장에 돈을 충전해주세요")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            exchangeRepository.createExchangeReservation(
                boxId = currentState.boxId, // 선택한 박스 ID 사용
                fromCurrency = "KRW",
                toCurrency = currentState.currencyCode,
                amount = currentState.amount,
                targetRate = currentState.targetRate,
                expiresAt = currentState.expiresAt
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "예약 생성에 실패했습니다"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}