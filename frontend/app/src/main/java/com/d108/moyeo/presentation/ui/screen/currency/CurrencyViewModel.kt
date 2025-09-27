package com.d108.moyeo.presentation.ui.screen.currency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.core.BoxStore
import com.d108.moyeo.data.mapper.toUi
import com.d108.moyeo.data.remote.dto.exchange.CurrencyResponseDto
import com.d108.moyeo.domain.model.exchange.Currency
import com.d108.moyeo.domain.repository.ExchangeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository,
    private val boxStore: BoxStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState = _uiState.asStateFlow()

    val boxes = boxStore.boxUiStates

    init {
        loadExchangeRates()
    }

    /**
     * API에서 환율 정보 로드
     */
    private fun loadExchangeRates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            exchangeRepository.getCurrencies()
                .onSuccess { map ->
                    val listForUi = map.values
                        .map { it.toUi() }
                    _uiState.update {
                        it.copy(
                            ratesList = listForUi,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "알 수 없는 오류가 발생했습니다."
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

    fun showModal(rate: CurrencyResponseDto) {
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

    fun addExchangeRate(newRate: CurrencyResponseDto) {
        val currentList = _uiState.value.ratesList.toMutableList()
        currentList.add(newRate)
        _uiState.update { it.copy(ratesList = currentList) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}