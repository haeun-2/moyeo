package com.d108.moyeo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.d108.moyeo.presentation.ui.component.exchange.ExchangeRateData

data class ExchangeUiState(
    val ratesList: List<ExchangeRateData> = emptyList(),
    val isEditMode: Boolean = false,
    val showModal: Boolean = false,
    val draggedItem: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ExchangeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ExchangeUiState())
    val uiState: StateFlow<ExchangeUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        // 초기 샘플 데이터 로드
        val initialRates = listOf(
            ExchangeRateData("🇯🇵", "보스니아 헤르체고비나", "927 JPY = 1,000 KRW", "6.15 (+0.59%)", true),
            ExchangeRateData("🇺🇸", "미국 달러", "1,340 USD = 1,000 KRW", "15.20 (+1.15%)", true),
            ExchangeRateData("🇪🇺", "유럽 유로", "1,450 EUR = 1,000 KRW", "8.30 (-0.58%)", false),
            ExchangeRateData("🇨🇳", "중국 위안", "185 CNY = 1,000 KRW", "2.10 (+0.23%)", true),
            ExchangeRateData("🇬🇧", "영국 파운드", "1,650 GBP = 1,000 KRW", "12.80 (-0.78%)", false)
        )

        _uiState.value = _uiState.value.copy(ratesList = initialRates)
    }

    fun toggleEditMode() {
        _uiState.value = _uiState.value.copy(
            isEditMode = !_uiState.value.isEditMode
        )
    }

    fun showModal() {
        _uiState.value = _uiState.value.copy(showModal = true)
    }

    fun hideModal() {
        _uiState.value = _uiState.value.copy(showModal = false)
    }

    fun deleteRate(index: Int) {
        val currentList = _uiState.value.ratesList.toMutableList()
        if (index < currentList.size) {
            currentList.removeAt(index)
            _uiState.value = _uiState.value.copy(ratesList = currentList)
        }
    }

    fun startDrag(index: Int) {
        _uiState.value = _uiState.value.copy(draggedItem = index)
    }

    fun endDrag(targetIndex: Int) {
        val draggedIndex = _uiState.value.draggedItem
        if (draggedIndex != null && draggedIndex != targetIndex) {
            val currentList = _uiState.value.ratesList.toMutableList()
            val draggedItem = currentList.removeAt(draggedIndex)
            currentList.add(targetIndex, draggedItem)

            _uiState.value = _uiState.value.copy(
                ratesList = currentList,
                draggedItem = null
            )
        } else {
            _uiState.value = _uiState.value.copy(draggedItem = null)
        }
    }

    fun addExchangeRate(newRate: ExchangeRateData) {
        val currentList = _uiState.value.ratesList.toMutableList()
        currentList.add(newRate)
        _uiState.value = _uiState.value.copy(ratesList = currentList)
    }

    fun refreshExchangeRates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // TODO: API 호출로 실제 환율 데이터 가져오기
                // val rates = exchangeRepository.getExchangeRates()
                // _uiState.value = _uiState.value.copy(ratesList = rates, isLoading = false)

                // 임시로 현재 데이터 유지
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}