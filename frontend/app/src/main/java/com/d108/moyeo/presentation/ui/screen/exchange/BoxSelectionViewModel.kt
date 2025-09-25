package com.d108.moyeo.presentation.ui.screen.exchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.core.BoxStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BoxSelectionUiState(
    val selectedBoxId: Long = -1L,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class BoxSelectionViewModel @Inject constructor(
    private val boxStore: BoxStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(BoxSelectionUiState())
    val uiState = _uiState.asStateFlow()

    // BoxStore에서 모든 박스 가져오기 (개인박스 + 그룹박스)
    val availableBoxes = boxStore.boxUiStates

    init {
        // 박스가 하나뿐이라면 자동으로 선택 (개인박스든 그룹박스든)
        viewModelScope.launch {
            availableBoxes.collect { boxes ->
                if (boxes.size == 1 && _uiState.value.selectedBoxId == -1L) {
                    onBoxSelected(boxes.first().id)
                }
            }
        }
    }

    fun onBoxSelected(boxId: Long) {
        _uiState.update { it.copy(selectedBoxId = boxId) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}