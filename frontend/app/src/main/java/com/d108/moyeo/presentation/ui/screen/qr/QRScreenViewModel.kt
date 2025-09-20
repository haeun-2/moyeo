package com.d108.moyeo.presentation.ui.screen.qr

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.core.BoxStore
import com.d108.moyeo.domain.usecase.box.GetBookmarkedBoxesUseCase
import com.d108.moyeo.domain.usecase.payment.GenerateQRTokenUseCase
import com.d108.moyeo.util.generateQRCodeBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private val TAG = "QRScreenViewModel"
@HiltViewModel
class QRScreenViewModel @Inject constructor(
    private val generateQRTokenUseCase: GenerateQRTokenUseCase,
    private val boxStore: BoxStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(QRScreenUiState())
    val uiState = _uiState.asStateFlow()

    val bookmarkedBoxes = boxStore.boxUiStates.map { allBoxes ->
        allBoxes.filter { it.isBookmarked }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private var timerJob: Job? = null  // 현재 실행 중인 타이머 작업


    // 사용자가 모여박스를 클릭했을 때 호출될 함수
    fun selectBox(boxId: Long) {

        timerJob?.cancel()  // 타이머가 실행 중이라면 취소하고
        _uiState.update { it.copy(isTimerRunning = false, timerText = "00:30") }  // 초기 상태로 돌림

        val currentSelectedId = _uiState.value.selectedBoxId
        val newSelectedId = if (currentSelectedId == boxId) null else boxId

        _uiState.update { it.copy(selectedBoxId = newSelectedId, qrImageBitmap = null) }

        // 새로운 박스가 선택되었을 때만 QR 코드 생성을 요청.
        if (newSelectedId != null) {
            generateQRCode(newSelectedId)
        }
    }

    fun selectBoxOnReturn(boxIdToSelect: Long) {
        selectBox(boxIdToSelect)
    }

    /*
    QR 코드 관련
     */

    fun onRefreshQRClick() {
        // 현재 선택된 박스가 있을 때만 새로고침을 실행합니다.
        _uiState.value.selectedBoxId?.let {
            generateQRCode(it)
        }
    }

    private fun generateQRCode(boxId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingQR = true, errorMessage = null) }
            generateQRTokenUseCase(boxId)
                .onSuccess { token ->
                    Log.d(TAG, "생성된 QR 토큰: $token")
                    // 서버로부터 토큰을 성공적으로 받아오면, QR 코드 이미지로 변환
                    val bitmap = generateQRCodeBitmap(token)
                    _uiState.update { it.copy(isLoadingQR = false, qrImageBitmap = bitmap) }

                    startTimer()  // QR 생성 성공 시 타이머 시작
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoadingQR = false, errorMessage = "QR 코드 생성에 실패했습니다.") }
                }
        }
    }


    /*
    타이머 관련
     */
    private fun startTimer() {
        // 기존 타이머 작업을 취소하고 새로 시작
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _uiState.update { it.copy(isTimerRunning = true) }
            for (i in 30 downTo 0) {
                _uiState.update { it.copy(timerText = "00:${String.format("%02d", i)}") }
                delay(1000L) // 1초 대기
            }
            // 타이머가 끝나면 QR 코드와 관련 상태를 모두 초기화
            _uiState.update {
                it.copy(
                    isTimerRunning = false,
                    qrImageBitmap = null,
                    selectedBoxId = null, // 선택도 함께 해제
                    timerText = "00:30"
                )
            }
        }
    }

}