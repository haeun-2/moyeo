package com.d108.moyeo.presentation.ui.screen.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.usecase.box.GetBookmarkedBoxesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QRScreenViewModel @Inject constructor(
    private val getBookmarkedBoxesUseCase: GetBookmarkedBoxesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QrScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBookmarkedBoxes()
    }

    // 사용자가 모여박스를 클릭했을 때 호출될 함수
    fun selectBox(boxId: Long) {
        _uiState.update { currentState ->
            val newSelectedId = if (currentState.selectedBoxId == boxId) null else boxId
            currentState.copy(selectedBoxId = newSelectedId)
        }
        // TODO: 여기서 서버에 QR 토큰 생성을 요청하는 로직이 추가되어야 합니다.
        // 예: generateQrCode(newSelectedId)
    }

    private fun loadBookmarkedBoxes() {
        viewModelScope.launch {
            // 1. 로딩 상태를 true로 변경하여 UI에 알려줍니다.
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 2. UseCase를 실행하여 서버로부터 데이터를 가져옵니다.
            getBookmarkedBoxesUseCase()
                .onSuccess { boxes ->
                    // 3. 성공 시, 받아온 박스 목록으로 상태를 업데이트합니다.
                    _uiState.update { it.copy(isLoading = false, bookmarkedBoxes = boxes) }
                }
                .onFailure { error ->
                    // 4. 실패 시, 에러 메시지를 상태에 저장합니다.
                    _uiState.update { it.copy(isLoading = false, errorMessage = "즐겨찾기 목록을 불러오지 못했습니다.") }
                }
        }
    }

}