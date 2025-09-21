package com.d108.moyeo.presentation.ui.screen.home.wallet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.model.history.HistoryTransaction
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyWalletDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    // private val updateMemoUseCase: UpdateMemoUseCase // TODO: 메모 수정 UseCase 주입
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyWalletDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // 1. SavedStateHandle에서 JSON 문자열을 꺼냄.
        val transactionJson = savedStateHandle.get<String>("transactionJson")
        if (transactionJson != null) {
            // 2. JSON 문자열을 HistoryTransaction 객체로 변환
            val transaction = Gson().fromJson(transactionJson, HistoryTransaction::class.java)
            _uiState.update {
                it.copy(
                    transaction = transaction,
                    editedMemo = transaction.memo ?: "" // 초기 메모 설정
                )
            }
        }
    }

    // 사용자가 메모를 수정할 때마다 호출될 함수
    fun onMemoChanged(newMemo: String) {
        _uiState.update { it.copy(editedMemo = newMemo) }
    }

    // 확인 버튼을 눌렀을 때 호출될 함수
    fun saveChanges() {
        val transaction = _uiState.value.transaction ?: return
        val newMemo = _uiState.value.editedMemo

        viewModelScope.launch {
            // TODO: 실제 메모 수정 API를 호출하는 UseCase 실행
            // updateMemoUseCase(transaction.id, newMemo)
            //     .onSuccess { /* 성공 처리 */ }
            //     .onFailure { /* 실패 처리 */ }
        }
    }

    fun startEditingMemo() {
        _uiState.update {
            it.copy(
                isMemoEditing = true,
                editedMemo = it.transaction?.memo ?: "" // 원본 메모로 초기화
            )
        }
    }

    // 메모 편집 저장
    fun saveMemoEdit() {
        _uiState.update { it.copy(isMemoEditing = false) }
        // TODO: 실제 저장 로직
    }

    // 메모 편집 취소
    fun cancelMemoEdit() {
        _uiState.update {
            it.copy(
                isMemoEditing = false,
                editedMemo = it.transaction?.memo ?: "" // 원본으로 되돌리기
            )
        }
    }
}