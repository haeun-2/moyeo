package com.d108.moyeo.presentation.ui.screen.home.box

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// MyBoxDetailScreen의 UI 상태를 담는 데이터 클래스
data class MyBoxDetailUiState(
    val transaction: BoxTransaction? = null, // 로딩 중일 수 있으므로 nullable
    val selectedCategory: String = ""
)

class MyBoxDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MyBoxDetailUiState())
    val uiState = _uiState.asStateFlow()

    // 화면이 생성될 때 transactionId를 받아와 상세 정보를 로드.
    fun loadTransactionDetails(transactionId: String) {
        viewModelScope.launch {
            // TODO: 실제 앱에서는 이 transactionId를 사용하여 Repository를 통해 서버/DB에서 데이터를 조회해야 합니다.
            val transaction = findTransactionById(transactionId)
            _uiState.update {
                it.copy(
                    transaction = transaction,
                    selectedCategory = "미지정" // 모여박스 거래내역은 카테고리가 없으므로 초기값 설정
                )
            }
        }
    }

    // 카테고리 편집 바텀시트에서 새로운 카테고리를 선택했을 때 호출될 함수
    fun onCategoryChange(newCategory: String) {
        _uiState.update { it.copy(selectedCategory = newCategory) }
    }

    // "확인" 버튼을 눌렀을 때 호출될 함수
    fun saveChanges() {
        // TODO: 변경된 selectedCategory를 서버에 저장하는 API를 호출.
        val transactionId = _uiState.value.transaction?.id
        val newCategory = _uiState.value.selectedCategory
        println("Saving... Transaction ID: $transactionId, New Category: $newCategory")
    }

    // MyBoxViewModel에 있던 임시 데이터 생성 로직을 그대로 가져와 사용.
    private fun findTransactionById(transactionId: String): BoxTransaction? {
        val transactions = List(15) {
            BoxTransaction(
                id = it.toString(),
                date = "09.${String.format("%02d", 15 - it)}",
                description = if (it % 3 == 0) "김상훈" else if (it % 3 == 1) "이풍헌" else "박동찬",
                amount = "+ 50,${String.format("%03d", it * 100)} JPY",
                balance = "11${5 - it},${String.format("%03d", it * 100)} JPY"
            )
        }
        return transactions.find { it.id == transactionId }
    }
}