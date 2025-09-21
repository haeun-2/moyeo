package com.d108.moyeo.presentation.ui.screen.home.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// MyWalletDetailScreen의 UI 상태를 담는 데이터 클래스
//data class MyWalletDetailUiState(
//    val transaction: WalletTransaction? = null, // 로딩 중일 수 있으므로 nullable
//    val selectedCategory: String = ""
//)

class MyWalletDetailViewModel : ViewModel() {
//
//    private val _uiState = MutableStateFlow(MyWalletDetailUiState())
//    val uiState = _uiState.asStateFlow()
//
//    // 화면이 생성될 때 transactionId를 받아와 상세 정보를 로드합니다.
//    fun loadTransactionDetails(transactionId: String) {
//        viewModelScope.launch {
//            // TODO: 실제 앱에서는 이 transactionId를 사용하여 Repository를 통해 서버/DB에서 데이터를 조회해야 합니다.
//            val transaction = findTransactionById(transactionId)
//            _uiState.update {
//                it.copy(
//                    transaction = transaction,
//                    selectedCategory = transaction?.category ?: "미지정" // 초기 카테고리 설정
//                )
//            }
//        }
//    }
//
//    // 카테고리 편집 바텀시트에서 새로운 카테고리를 선택했을 때 호출될 함수
//    fun onCategoryChange(newCategory: String) {
//        _uiState.update { it.copy(selectedCategory = newCategory) }
//    }
//
//    // "확인" 버튼을 눌렀을 때 호출될 함수
//    fun saveChanges() {
//        // TODO: 변경된 selectedCategory를 서버에 저장하는 API를 호출해야 합니다.
//        val transactionId = _uiState.value.transaction?.id
//        val newCategory = _uiState.value.selectedCategory
//        println("Saving... Transaction ID: $transactionId, New Category: $newCategory")
//    }
//
//    // MyWalletViewModel에 있던 임시 데이터 생성 로직을 그대로 가져와 사용합니다.
//    /*
//    나중에 실제 서버가 완성되면,
//    이 함수의 내부 로직은 repository.getTransactionFromServer(transactionId) 와 같이
//    실제 데이터를 가져오는 코드로 통째로 교체될 것
//     */
//    private fun findTransactionById(transactionId: String): WalletTransaction? {
//        val transactions = List(20) {
//            WalletTransaction(
//                id = it.toString(),
//                date = "09.${String.format("%02d", 10 - it)}",
//                description = if (it % 2 == 0) "일본 여행" else "GS25 편의점",
//                amount = "- 5,${String.format("%03d", it * 100)} 원",
//                balance = "11${5 - it},${String.format("%03d", it * 100)} 원",
//                timestamp = "2025.09.${String.format("%02d", 10 - it)} 13:42",
//                category = if (it % 2 == 0) "여행" else "식/음료"
//            )
//        }
//        return transactions.find { it.id == transactionId }
//    }
}