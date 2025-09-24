package com.d108.moyeo.presentation.ui.screen.exchange.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.usecase.exchange.history.GetExchangeRateHistoryUseCase
import com.d108.moyeo.domain.usecase.exchange.history.GetExchangeVolumeHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


private val TAG = "ExchangeHistoryViewModel"
@HiltViewModel
class ExchangeHistoryViewModel @Inject constructor(
    private val getExchangeRateHistoryUseCase: GetExchangeRateHistoryUseCase,
    private val getExchangeVolumeHistoryUseCase: GetExchangeVolumeHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExchangeHistoryUiState())
    val uiState = _uiState.asStateFlow()

    fun fetchChartData(currencyCode: String, currencyName: String, unit: String?) {
        viewModelScope.launch {
            // API 호출 전에, 전달받은 정보로 UI 상태를 먼저 업데이트하고 로딩을 시작합니다.
            _uiState.update {
                it.copy(
                    isLoading = true,
                    currencyCode = currencyCode,
                    currencyName = currencyName
                )
            }

            val rateResultDeferred = async { getExchangeRateHistoryUseCase(unit, currencyCode) }
            val volumeUnit = when (unit) {
                "1h" -> "h"
                "1d" -> "d"
                "10m" -> "m"
                else -> "m"}
            val volumeResultDeferred = async { getExchangeVolumeHistoryUseCase(volumeUnit, currencyCode) }

            val rateResult = rateResultDeferred.await()
            val volumeResult = volumeResultDeferred.await()

            rateResult.onSuccess { rateHistory ->
                volumeResult.onSuccess { volumeHistory ->
                    Log.d(TAG, "rateHistory: $rateHistory")
                    Log.d(TAG, "volumeHistory: $volumeHistory")
                    _uiState.update {
                        it.copy(
                            // isLoading과 API 결과 데이터만 업데이트합니다.
                            isLoading = false,
                            exchangeRateHistoryData = rateHistory,
                            exchangeVolumeHistoryData = volumeHistory,
                            errorMessage = null
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = exception.message) }
                }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, errorMessage = exception.message) }
            }
        }
    }
}
