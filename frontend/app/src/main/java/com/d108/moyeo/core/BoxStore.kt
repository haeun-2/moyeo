package com.d108.moyeo.core

import androidx.compose.ui.graphics.Color
import com.d108.moyeo.presentation.ui.screen.home.transfer.CurrencyData
import com.d108.moyeo.util.textColorUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 앱 전역에서 사용되는 모든 박스(개인 지갑, 그룹 박스)의 UI 상태를 관리하는 중앙 저장소
 *
 * HomeViewModel이 서버/로컬 데이터를 조합하여 이 Store를 초기화/갱신하며,
 * 다른 모든 화면(ViewModel)은 이 Store의 `boxUiStates` Flow를 구독하여 최신 데이터를 사용
 *
 * 이 클래스는 앱의 메모리 내 캐시 역할을 하므로, 여기서 직접 API를 호출하지 않음
 *
 * @see HomeViewModel - 이 Store의 데이터를 생성하고 공급하는 유일한 ViewModel.
 */
@Singleton
class BoxStore @Inject constructor() {

    private val _boxUiStates = MutableStateFlow<List<BoxStoreUiState>>(emptyList())
    val boxUiStates: StateFlow<List<BoxStoreUiState>> = _boxUiStates.asStateFlow()

    // CurrencyData UiState 적용
    private val _personalCurrencies = MutableStateFlow<List<CurrencyData>>(emptyList())
    val personalCurrencies: StateFlow<List<CurrencyData>> = _personalCurrencies.asStateFlow()

    // 그룹 박스 정보 저장
    fun setUiStates(list: List<BoxStoreUiState>) {
        _boxUiStates.value = list
    }

    fun patchBox(
        id: Long,
        newName: String? = null,
        newBg: Color? = null,
        newIsBookmarked: Boolean? = null
    ) {
        val updatedUiStates = _boxUiStates.value.map { boxState ->
            if (boxState.id == id) {
                val finalBg = newBg ?: boxState.bg
                boxState.copy(
                    title = newName ?: boxState.title,
                    bg = finalBg,
                    textColor = textColorUtil(finalBg),
                    isBookmarked = newIsBookmarked ?: boxState.isBookmarked
                )
            } else {
                boxState
            }
        }
        _boxUiStates.value = updatedUiStates
    }


    // 개인 박스 통화 종류 저장
    // 통화량 등의 민감 정보는 배제
    fun setPersonalCurrencies(list: List<CurrencyData>) {
        _personalCurrencies.value = list  //
    }

    // BoxStore 초기화
    fun clear() {
        _boxUiStates.value = emptyList()
        _personalCurrencies.value = emptyList()
    }
}
