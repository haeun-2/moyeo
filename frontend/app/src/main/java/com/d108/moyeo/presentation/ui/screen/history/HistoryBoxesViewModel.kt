package com.d108.moyeo.presentation.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.usecase.box.GetGroupBoxesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HistoryBoxesNavEvent {
    data class NavigateBackWithResult(val selectedBoxId: Long) : HistoryBoxesNavEvent()
}

@HiltViewModel
class HistoryBoxesViewModel @Inject constructor(
    private val getGroupBoxesUseCase: GetGroupBoxesUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(HistoryBoxesUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HistoryBoxesNavEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        loadAllBoxes()
    }

    /**
     * 서버로부터 전체 모임 박스 목록을 불러오도록
     */
    private fun loadAllBoxes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getGroupBoxesUseCase(size = 50)
                .onSuccess { boxes ->
                    _uiState.update { it.copy(isLoading = false, allBoxes = boxes) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = "전체 박스 목록을 불러오지 못했습니다.") }
                }
        }
    }

    fun onBoxSelected(boxId: Long) {
        _uiState.update { currentState ->
            // 이미 선택된 박스를 다시 누르면 선택 해제, 다른 박스를 누르면 선택 변경
            val newSelectedId = if (currentState.newlySelectedBoxId == boxId) null else boxId
            currentState.copy(newlySelectedBoxId = newSelectedId)
        }
    }

    fun onConfirmClick() {
        val selectedId = _uiState.value.newlySelectedBoxId ?: return

        viewModelScope.launch {
            _navigationEvent.emit(HistoryBoxesNavEvent.NavigateBackWithResult(selectedId))
        }
    }
}