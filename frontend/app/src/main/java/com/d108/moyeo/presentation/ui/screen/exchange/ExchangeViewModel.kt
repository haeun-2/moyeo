package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.repository.ExchangeRepository
import com.d108.moyeo.presentation.ui.component.exchange.ExchangeRateData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map
import com.d108.moyeo.presentation.ui.screen.exchange.ExchangeUiState

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
                .onSuccess { exchangeRates ->
                    val exchangeRateDataList = exchangeRates.map { exchangeRate ->
                        ExchangeRateData(
                            countryFlag = exchangeRate.countryFlag,
                            bankName = exchangeRate.currencyName,
                            rate = "${exchangeRate.originalRate.toInt()} ${exchangeRate.currencyCode} = 1,000 KRW",
                            change = exchangeRate.changeRate,
                            isIncreased = exchangeRate.isIncreased
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
                    // 에러 발생 시 샘플 데이터 로드
                    loadSampleData()
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
    private fun loadSampleData() {
        val sampleRates = listOf(
            ExchangeRateData("🇯🇵", "일본 엔", "927 JPY = 1,000 KRW", "6.15 (+0.59%)", true),
            ExchangeRateData("🇺🇸", "미국 달러", "1,340 USD = 1,000 KRW", "15.20 (+1.15%)", true),
            ExchangeRateData("🇪🇺", "유럽 유로", "1,450 EUR = 1,000 KRW", "8.30 (-0.58%)", false),
            ExchangeRateData("🇨🇳", "중국 위안", "185 CNY = 1,000 KRW", "2.10 (+0.23%)", true),
            ExchangeRateData("🇬🇧", "영국 파운드", "1,650 GBP = 1,000 KRW", "12.80 (-0.78%)", false)
        )

        _uiState.update { it.copy(ratesList = sampleRates) }
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