package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ReservationAmountInputUiState(
    val currencyCode: String = "",
    val currencyName: String = "",
    val targetRate: Long = 0L,
    val inputAmount: String = "0",
    val selectedTab: String = "JPY",
    val convertedAmount: String = "0",
    val errorMessage: String? = null,
    val krwValue: Double = 0.0,
    val foreignValue: Double = 0.0,
    val krwBalance: Double = 0.0,
)

@HiltViewModel
class ReservationAmountInputViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationAmountInputUiState())
    val uiState = _uiState.asStateFlow()

    fun initialize(currencyCode: String, currencyName: String, targetRate: Long) {
        _uiState.update {
            it.copy(
                currencyCode = currencyCode,
                currencyName = currencyName,
                targetRate = targetRate,
                selectedTab = currencyCode,
                inputAmount = "0",
                krwValue = 0.0,
                foreignValue = 0.0
            )
        }
    }

    fun onDigitInput(digit: String) {
        val currentAmount = _uiState.value.inputAmount

        var newAmount = currentAmount
        if (currentAmount == "0" && digit != "00") {
            newAmount = digit
        } else if (currentAmount.isEmpty() && digit == "00") {
            return
        } else if (currentAmount == "0" && digit == "00") {
            return
        } else if ((currentAmount + digit).length > 10) {
            return
        } else {
            newAmount = currentAmount + digit
        }

        updateConversion(newAmount)
    }

    fun onBackspace() {
        val currentAmount = _uiState.value.inputAmount
        val newAmount = if (currentAmount.length <= 1) "0" else currentAmount.dropLast(1)
        updateConversion(newAmount)
    }

    fun selectTab(tab: String) {
        val state = _uiState.value
        var newInput = state.inputAmount

        if (tab == "KRW" && state.selectedTab != "KRW") {
            // 외화 → 원화
            val foreignAmount = state.inputAmount.toDoubleOrNull() ?: 0.0
            val krwValue = foreignAmount * state.targetRate
            newInput = krwValue.toLong().toString()
        } else if (tab == state.currencyCode && state.selectedTab == "KRW") {
            // 원화 → 외화
            val krwAmount = state.inputAmount.toDoubleOrNull() ?: 0.0
            val foreignValue = krwAmount / state.targetRate
            newInput = foreignValue.toLong().toString()
        }

        _uiState.update {
            it.copy(
                selectedTab = tab,
                inputAmount = newInput
            )
        }
        updateConversion(newInput)
    }

    private fun updateConversion(newAmount: String) {
        val state = _uiState.value
        val amount = newAmount.toDoubleOrNull() ?: 0.0
        val krwValue: Double
        val foreignValue: Double

        if (state.selectedTab == state.currencyCode) {
            // 외화 입력
            foreignValue = amount
            krwValue = foreignValue * state.targetRate
        } else {
            // 원화 입력
            krwValue = amount
            foreignValue = if (state.targetRate != 0L) krwValue / state.targetRate else 0.0
        }

        _uiState.update {
            it.copy(
                inputAmount = newAmount,
                krwValue = krwValue,
                foreignValue = foreignValue
            )
        }
    }
}
