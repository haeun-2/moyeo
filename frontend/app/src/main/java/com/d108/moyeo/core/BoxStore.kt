package com.d108.moyeo.core

import androidx.compose.ui.graphics.Color
import com.d108.moyeo.data.local.UserDataManager
import com.d108.moyeo.data.mapper.toBoxStoreUiState
import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.usecase.box.GetGroupBoxesUseCase
import com.d108.moyeo.domain.usecase.box.GetPersonalBoxUseCase
import com.d108.moyeo.presentation.ui.screen.home.transfer.CurrencyData
import com.d108.moyeo.util.CurrencyUtils.getCurrencyName
import com.d108.moyeo.util.textColorUtil
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
class BoxStore @Inject constructor(
    private val getPersonalBoxUseCase: GetPersonalBoxUseCase,
    private val getGroupBoxesUseCase: GetGroupBoxesUseCase,
    private val userDataManager: UserDataManager
) {

    private val _boxUiStates = MutableStateFlow<List<BoxStoreUiState>>(emptyList())
    val boxUiStates: StateFlow<List<BoxStoreUiState>> = _boxUiStates.asStateFlow()

    // CurrencyData UiState 적용
    private val _personalCurrencies = MutableStateFlow<List<CurrencyData>>(emptyList())
    val personalCurrencies: StateFlow<List<CurrencyData>> = _personalCurrencies.asStateFlow()

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


    suspend fun refreshBoxes() {
        coroutineScope {
            val personalBoxResultDeferred = async { getPersonalBoxUseCase() }
            val groupBoxesResultDeferred = async { getGroupBoxesUseCase(size = 30) }

            val personalResult = personalBoxResultDeferred.await()
            val groupResult = groupBoxesResultDeferred.await()

            val allServerBoxes = mutableListOf<Box>()
            personalResult.onSuccess { allServerBoxes.add(it) }
            groupResult.onSuccess { allServerBoxes.addAll(it) }

            val finalUiStateList = allServerBoxes.map { serverBox ->
                async { serverBox.toBoxStoreUiState(userDataManager) }
            }.awaitAll()

            // BoxStore의 내부 상태를 직접 업데이트
            _boxUiStates.value = finalUiStateList

            personalResult.onSuccess { personalBox ->
                val currencies = personalBox.balances
                    .map { CurrencyData(name = getCurrencyName(it.currency), code = it.currency) }
                _personalCurrencies.value = currencies
            }
        }
    }

    // BoxStore 초기화
    fun clear() {
        _boxUiStates.value = emptyList()
        _personalCurrencies.value = emptyList()
    }
}
