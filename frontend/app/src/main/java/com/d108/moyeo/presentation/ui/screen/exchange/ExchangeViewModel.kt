package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.d108.moyeo.presentation.ui.component.exchange.ExchangeRateData

// Exchange 화면의 모든 UI 상태를 담는 데이터 클래스
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
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        // 샘플 데이터 로드 (이후 Repository에서 가져와야 함)
        val initialRates = listOf(
            ExchangeRateData("🇯🇵", "보스니아 헤르체고비나", "927 JPY = 1,000 KRW", "6.15 (+0.59%)", true),
            ExchangeRateData("🇺🇸", "미국 달러", "1,340 USD = 1,000 KRW", "15.20 (+1.15%)", true),
            ExchangeRateData("🇪🇺", "유럽 유로", "1,450 EUR = 1,000 KRW", "8.30 (-0.58%)", false),
            ExchangeRateData("🇨🇳", "중국 위안", "185 CNY = 1,000 KRW", "2.10 (+0.23%)", true),
            ExchangeRateData("🇬🇧", "영국 파운드", "1,650 GBP = 1,000 KRW", "12.80 (-0.78%)", false)
        )

        _uiState.update { it.copy(ratesList = initialRates) }
    }

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditMode = !it.isEditMode) }
    }

    fun showModal() {
        _uiState.update { it.copy(showModal = true) }
    }

    fun hideModal() {
        _uiState.update { it.copy(showModal = false) }
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

    fun addExchangeRate(newRate: ExchangeRateData) {
        val currentList = _uiState.value.ratesList.toMutableList()
        currentList.add(newRate)
        _uiState.update { it.copy(ratesList = currentList) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}