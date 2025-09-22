package com.d108.moyeo.presentation.ui.screen.home.wallet

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.model.history.HistoryTransaction
import com.d108.moyeo.domain.usecase.history.GetExchangeHistoryDetailUseCase
import com.d108.moyeo.domain.usecase.history.UpdateHistoryUseCase
import com.d108.moyeo.presentation.ui.component.home.FilterOptionData
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder
import javax.inject.Inject

sealed class MyWalletDetailNavEvent {
    data class NavigateBackWithSearchQuery(val query: String) : MyWalletDetailNavEvent()
    data class NavigateBackWithSearchCategory(val category: String) : MyWalletDetailNavEvent()
}


@HiltViewModel
class MyWalletDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val updateHistoryUseCase: UpdateHistoryUseCase,
    private val getExchangeHistoryDetailUseCase: GetExchangeHistoryDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyWalletDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<MyWalletDetailNavEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val boxId = savedStateHandle.get<Long>("boxId") ?: -1L


    init {
        // 1. SavedStateHandle에서 JSON 문자열을 꺼냄.
        val transactionJson = savedStateHandle.get<String>("transactionJson")
        if (transactionJson != null) {
            // 2. JSON 문자열을 HistoryTransaction 객체로 변환
            val decodedJson = URLDecoder.decode(transactionJson, "UTF-8")
            val transaction = Gson().fromJson(decodedJson, HistoryTransaction::class.java)
            _uiState.update {
                it.copy(
                    transaction = transaction,
                    editedMemo = transaction.memo ?: "", // 초기 메모 설정
                    selectedCategory = transaction.category
                )
            }

            // 환전 카테고리면 추가로 호출
            if (transaction.category == "환전") {
                fetchExchangeDetail(transaction.id)
            }
        }
    }

    // --- 카테고리 관련 이벤트 핸들러 ---
    fun onCategoryEditClick() {
        _uiState.update { it.copy(showCategorySheet = true) }
    }

    fun onCategorySheetDismiss() {
        _uiState.update { it.copy(showCategorySheet = false) }
    }

    fun onCategorySelected(newCategoryName: String) {
        val transaction = _uiState.value.transaction ?: return
        val oldCategoryName = transaction.category

        _uiState.update {
            it.copy(
                showCategorySheet = false,
                transaction = it.transaction?.copy(category = newCategoryName)
            )
        }

        viewModelScope.launch {
            val newCategoryId = FilterOptionData.allScopeOptions.indexOf(newCategoryName).toLong()

            updateHistoryUseCase(
                boxId = boxId,
                historyId = transaction.id,
                categoryId = newCategoryId
            ).onFailure {
                // 3. 실패 시 롤백
                _uiState.update { it.copy(transaction = it.transaction?.copy(category = oldCategoryName)) }
            }
        }
    }

    // 사용자가 메모를 수정할 때마다 호출될 함수
    fun onMemoChanged(newMemo: String) {
        _uiState.update { it.copy(editedMemo = newMemo) }
    }

    fun startEditingMemo() {
        _uiState.update {
            it.copy(
                isMemoEditing = true,
                editedMemo = it.transaction?.memo ?: "" // 원본 메모로 초기화
            )
        }
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

    fun saveMemoEdit() {
        val transaction = _uiState.value.transaction ?: return
        val oldMemo = transaction.memo ?: ""
        val newMemo = _uiState.value.editedMemo

        _uiState.update {
            it.copy(
                isMemoEditing = false,
                transaction = it.transaction?.copy(memo = newMemo)
            )
        }

        viewModelScope.launch {
            updateHistoryUseCase(
                boxId = boxId,
                historyId = transaction.id,
                memo = newMemo
            ).onFailure {
                // 3. 실패 시 롤백
                _uiState.update { it.copy(transaction = it.transaction?.copy(memo = oldMemo)) }
            }
        }
    }

    fun onSearchTitleClick() {
        val title = uiState.value.transaction?.title ?: return
        viewModelScope.launch {
            _navigationEvent.emit(MyWalletDetailNavEvent.NavigateBackWithSearchQuery(title))
        }
    }

    fun onSearchCategoryClick() {
        val category = uiState.value.transaction?.category ?: return
        viewModelScope.launch {
            _navigationEvent.emit(MyWalletDetailNavEvent.NavigateBackWithSearchCategory(category))
        }
    }

    private fun fetchExchangeDetail(historyId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            Log.d("MyWalletDetailViewModel", "fetchExchangeDetail: 박스 아이디: $boxId, 히스토리 아이디: $historyId")
            getExchangeHistoryDetailUseCase(boxId = boxId, historyId = historyId)
                .onSuccess { details ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            exchangeDetails = details
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "환전 정보를 불러오지 못했습니다.") }
                }
        }
    }

}