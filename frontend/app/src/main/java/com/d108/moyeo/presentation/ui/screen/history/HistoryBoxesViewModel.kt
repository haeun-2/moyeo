package com.d108.moyeo.presentation.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.core.BoxStore
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
    private val boxStore: BoxStore
): ViewModel() {
    private val _uiState = MutableStateFlow(HistoryBoxesUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HistoryBoxesNavEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    val allBoxesUi = boxStore.boxUiStates

    fun onBoxSelected(boxId: Long) {
        _uiState.update { currentState ->
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