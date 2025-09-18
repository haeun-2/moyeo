package com.d108.moyeo.core

import androidx.compose.ui.graphics.Color
import com.d108.moyeo.presentation.ui.screen.home.GroupBox
import com.d108.moyeo.presentation.ui.screen.home.sending.CurrencyData
import com.d108.moyeo.util.textColorUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoxStore @Inject constructor() {

    // GroupBox UiState 를 받아와서 BoxStoreUiState 에 맞게 가공 (textColor 수정 위함)
    private val _groupBoxes = MutableStateFlow<List<GroupBox>>(emptyList())
    private val _groupBoxesUi = MutableStateFlow<List<BoxStoreUiState>>(emptyList())
    val groupBoxesUi: StateFlow<List<BoxStoreUiState>> = _groupBoxesUi.asStateFlow()

    // CurrencyData UiState 적용
    private val _personalCurrencies = MutableStateFlow<List<CurrencyData>>(emptyList())
    val personalCurrencies: StateFlow<List<CurrencyData>> = _personalCurrencies.asStateFlow()

    // 그룹 박스 정보 저장
    fun setGroupBoxes(list: List<GroupBox>) {
        _groupBoxes.value = list
        _groupBoxesUi.value = list.map { it.toUi() }
    }

    // groupBox 의 정보가 바뀌었을 때(이름, 색상) BoxStore 에 반영
    // 통화량 등의 민감 정보는 배제
    fun patchBox(
        id: String,
        newName: String? = null,
        newBg: Color? = null
    ) {
        val updated = _groupBoxes.value.map { box ->
            if (box.id == id) box.copy(
                title = newName ?: box.title,
                bg = newBg ?: box.bg
            ) else box
        }
        _groupBoxes.value = updated
        _groupBoxesUi.value = updated.map { it.toUi() }
    }

    // textColor 추가
    private fun GroupBox.toUi() = BoxStoreUiState(
        id = id,
        title = title,
        bg = bg,
        textColor = textColorUtil(bg),
    )

    // 개인 박스 통화 종류 저장
    // 통화량 등의 민감 정보는 배제
    fun setPersonalCurrencies(list: List<CurrencyData>) {
        _personalCurrencies.value = list
    }

    // BoxStore 초기화
    fun clear() {
        _groupBoxes.value = emptyList()
        _groupBoxesUi.value = emptyList()
        _personalCurrencies.value = emptyList()
    }
}
