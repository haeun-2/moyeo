package com.d108.moyeo.presentation.ui.screen.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.usecase.box.AddBookmarkUseCase
import com.d108.moyeo.domain.usecase.box.GetGroupBoxesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class QRBoxesNavEvent {
    // 즐겨찾기 추가 성공 시, 이전 화면에 새로 추가된 박스 ID를 알려주며 돌아감
    data class NavigateBackWithResult(val selectedBoxId: Long) : QRBoxesNavEvent()
}

@HiltViewModel
class QRBoxesViewModel @Inject constructor(
    private val getGroupBoxesUseCase: GetGroupBoxesUseCase,
    private val addBookmarkUseCase: AddBookmarkUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(QRBoxesUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<QRBoxesNavEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()


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

    fun onBoxClick(box: Box) {
        // 이미 북마크된 박스는 선택 불가
        if (box.isBookmarked) return

        _uiState.update { currentState ->
            // 클릭한 박스가 이미 선택된 상태이면 선택 해제, 아니면 새로 선택
            val newSelectedId =
                if (currentState.newlySelectedBoxId == box.id)
                    null
                else
                    box.id
            currentState.copy(newlySelectedBoxId = newSelectedId)
        }
    }

    fun onConfirmClick() {
        val selectedId = _uiState.value.newlySelectedBoxId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            addBookmarkUseCase(selectedId)
                .onSuccess {
                    _navigationEvent.emit(QRBoxesNavEvent.NavigateBackWithResult(selectedId))
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = "즐겨찾기 추가에 실패했습니다.") }
                }
        }
    }
}