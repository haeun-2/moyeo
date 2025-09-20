package com.d108.moyeo.presentation.ui.screen.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.core.BoxStore
import com.d108.moyeo.core.BoxStoreUiState
import com.d108.moyeo.data.local.UserDataManager
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
    data class NavigateBackWithResult(val selectedBoxId: Long) : QRBoxesNavEvent()
}

@HiltViewModel
class QRBoxesViewModel @Inject constructor(
    private val addBookmarkUseCase: AddBookmarkUseCase,
    private val boxStore: BoxStore,  // 새롭게 박스 스토어를 주입받음
    private val userDataManager: UserDataManager // 로컬 저장을 위해 추가
) : ViewModel() {
    private val _uiState = MutableStateFlow(QRBoxesUiState())
    val uiState = _uiState.asStateFlow()
    val groupBoxesUi = boxStore.boxUiStates  // 이미 스테이트플로우 처리가 되어서 들어옴

    private val _navigationEvent = MutableSharedFlow<QRBoxesNavEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()


    fun onBoxClick(box: BoxStoreUiState) {
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

        // UI 업데이트
        boxStore.patchBox(id = selectedId, newIsBookmarked = true)

        // 2. 백그라운드에서 로컬 DB와 서버에 동기화합니다.
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } // 버튼 로딩 상태 표시

            // 로컬 저장
            userDataManager.addBookmark(selectedId)

            // 서버 요청
            addBookmarkUseCase(selectedId)
                .onSuccess {
                    // 서버 통신 성공 시, 값을 전달하며 닫음
                    _navigationEvent.emit(QRBoxesNavEvent.NavigateBackWithResult(selectedId))
                }
                .onFailure { error ->
                    // 서버 통신 실패 시, 모든 변경사항을 롤백
                    boxStore.patchBox(id = selectedId, newIsBookmarked = false)
                    userDataManager.removeBookmark(selectedId) // 로컬 저장도 취소
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "즐겨찾기 추가에 실패했습니다.")
                    }
                }
        }
    }
}