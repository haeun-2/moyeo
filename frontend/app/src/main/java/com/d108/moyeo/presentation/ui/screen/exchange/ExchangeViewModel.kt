package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.data.remote.dto.exchange.ExchangeRateItem
import com.d108.moyeo.domain.repository.ExchangeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExchangeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadExchangeRates()
    }

    /**
     * API에서 환율 정보 로드
     */
    private fun loadExchangeRates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            exchangeRepository.getCurrentExchangeRates()
                .onSuccess { ratesMap ->
                    val exchangeRateDataList = ratesMap.map { (currencyCode, rateItem) ->
                        ExchangeRateItem(
                            countryFlag = rateItem.countryFlag,
                            currencyCode = rateItem.currencyCode,
                            buyRate = rateItem.buyRate,
                            sellRate = rateItem.sellRate,
                            originalRate = rateItem.originalRate,
                        )
                    }
                    _uiState.update {
                        it.copy(
                            ratesList = exchangeRateDataList,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message
                        )
                    }
                }
        }
    }


    /**
     * 환율 정보 새로고침
     */
    fun refreshExchangeRates() {
        loadExchangeRates()
    }

    /**
     * 에러 발생 시 기본 샘플 데이터 로드
     */
    fun toggleEditMode() {
        _uiState.update { it.copy(isEditMode = !it.isEditMode) }
    }

    fun showModal(rate: ExchangeRateItem) {
        _uiState.update {
            it.copy(
                showModal = true,
                selectedItem = rate
            )
        }
    }

    fun hideModal() {
        _uiState.update {
            it.copy(
                showModal = false,
                selectedItem = null
            )
        }
    }

    fun deleteRate(index: Int) {
        val currentList = _uiState.value.ratesList.toMutableList()
        if (index < currentList.size) {
            currentList.removeAt(index)
            _uiState.update { it.copy(ratesList = currentList) }
        }
    }

    fun startDrag(index: Int) {
        _uiState.update { it.copy(draggedItem = index) }
    }

    fun endDrag(targetIndex: Int) {
        val draggedIndex = _uiState.value.draggedItem
        if (draggedIndex != null && draggedIndex != targetIndex) {
            val currentList = _uiState.value.ratesList.toMutableList()
            val draggedItem = currentList.removeAt(draggedIndex)
            currentList.add(targetIndex, draggedItem)

            _uiState.update {
                it.copy(
                    ratesList = currentList,
                    draggedItem = null
                )
            }
        } else {
            _uiState.update { it.copy(draggedItem = null) }
        }
    }

    fun addExchangeRate(newRate: ExchangeRateItem) {
        val currentList = _uiState.value.ratesList.toMutableList()
        currentList.add(newRate)
        _uiState.update { it.copy(ratesList = currentList) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}