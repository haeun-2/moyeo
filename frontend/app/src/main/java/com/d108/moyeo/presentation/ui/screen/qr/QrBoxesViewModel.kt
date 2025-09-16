package com.d108.moyeo.presentation.ui.screen.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.usecase.box.GetGroupBoxesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrBoxesViewModel @Inject constructor(
    private val getGroupBoxesUseCase: GetGroupBoxesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(QrBoxesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAllBoxes()
    }

    private fun loadAllBoxes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getGroupBoxesUseCase(size = 30)
                .onSuccess { boxes ->
                    _uiState.update { it.copy(isLoading = false, allBoxes = boxes) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = "전체 박스 목록을 불러오지 못했습니다.") }
                }
        }
    }
}