package com.d108.moyeo.presentation.ui.screen.qr

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.core.BoxStore
import com.d108.moyeo.core.BoxStoreUiState
import com.d108.moyeo.data.local.UserDataManager
import com.d108.moyeo.data.mapper.toBoxStoreUiState
import com.d108.moyeo.domain.model.box.BoxType
import com.d108.moyeo.domain.usecase.box.GetPaymentBoxesUseCase
import com.d108.moyeo.domain.usecase.payment.GenerateQRTokenUseCase
import com.d108.moyeo.util.generateQRCodeBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private val TAG = "QRScreenViewModel"
@HiltViewModel
class QRScreenViewModel @Inject constructor(
    private val generateQRTokenUseCase: GenerateQRTokenUseCase,
    private val getPaymentBoxesUseCase: GetPaymentBoxesUseCase,
    private val userDataManager: UserDataManager, // Mapper에 필요하므로 주입
    private val boxStore: BoxStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(QRScreenUiState())
    val uiState = _uiState.asStateFlow()


    // ✨ 3. 화면에 표시할 최종 박스 목록을 담을 StateFlow
    private val _bookmarkedPaymentBoxes = MutableStateFlow<List<BoxStoreUiState>>(emptyList())
    val bookmarkedPaymentBoxes = _bookmarkedPaymentBoxes.asStateFlow()

    private var timerJob: Job? = null  // 현재 실행 중인 타이머 작업

    init {
        loadBookmarkedPaymentBoxes()
    }

    private fun loadBookmarkedPaymentBoxes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingBoxes = true) }
            getPaymentBoxesUseCase()
                .onSuccess { paymentBoxes ->
                    // UI State로 변환
                    val uiStateBoxes = coroutineScope {
                        paymentBoxes.map { async { it.toBoxStoreUiState(userDataManager) } }.awaitAll()
                    }

                    // 개인 지갑이거나, 즐겨찾기된 박스만 필터링
                    val filteredBoxes = uiStateBoxes.filter { it.type == BoxType.PERSONAL || it.isBookmarked }
                    _bookmarkedPaymentBoxes.value = filteredBoxes
                    _uiState.update { it.copy(isLoadingBoxes = false) }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoadingBoxes = false, errorMessage = "박스 목록을 불러올 수 없습니다.") }
                }
        }
    }


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
        _uiState.update { it.copy(scrollToBoxId = boxIdToSelect) }
        loadBookmarkedPaymentBoxes()
        selectBox(boxIdToSelect)
    }

    fun onScrollCompleted() {
        _uiState.update { it.copy(scrollToBoxId = null) }
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