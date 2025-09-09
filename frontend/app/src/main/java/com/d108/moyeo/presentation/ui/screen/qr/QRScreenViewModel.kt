package com.d108.moyeo.presentation.ui.screen.qr

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


// 화폐 하나의 잔액을 나타내는 데이터 클래스
data class CurrencyBalance(
    val name: String,
    val amount: String
)

// 모여박스 하나의 정보를 담는 데이터 클래스  << 후에 domain/model로 이동
data class MoyeoBox(
    val id: String,
    val name: String,
    val icon: String, // 아이콘 리소스 이름 등을 담을 수 있습니다.
    val balances: List<CurrencyBalance>
)

// QR 화면의 모든 상태를 담는 데이터 클래스  << 후에 domain/modle로 이동
data class QrScreenUiState(
    val moyeoBoxes: List<MoyeoBox> = emptyList(),
    val selectedBoxId: String? = null
    // TODO: 나중에 QR 코드 Bitmap, 로딩 상태 등을 추가
)

class QRScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QrScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // 앱이 시작될 때 임시 모여박스 데이터를 로드
        // 실제로는 Repository를 통해 서버나 DB에서 데이터를 가져와야 함
        loadMoyeoBoxes()
    }

    // 사용자가 모여박스를 클릭했을 때 호출될 함수
    fun selectBox(boxId: String) {
        _uiState.update {
            // 이미 선택된 박스를 다시 누르면 선택 해제, 다른 박스를 누르면 선택 변경
            val newSelectedId = if (it.selectedBoxId == boxId) null else boxId
            it.copy(selectedBoxId = newSelectedId)
        }
        // TODO: 여기서 서버에 거래 ID를 요청하고 QR 코드를 생성하는 로직이 추가되어야 합니다.
    }

    private fun loadMoyeoBoxes() {
        // 임시 데이터 생성
        val boxes = List(5) { index ->
            MoyeoBox(
                id = "box_$index",
                name = "${index + 1}번 박스",
                icon = "ICON_NAME",
                balances = listOf(
                    CurrencyBalance("엔(JPY)", "${15000 * (index + 1)}"),
                    CurrencyBalance("원(KRW)", "${132000 * (index + 1)}"),
                    CurrencyBalance("엔(JPY)", "${15000 * (index + 1)}"),
                    CurrencyBalance("원(KRW)", "${132000 * (index + 1)}"),
                    CurrencyBalance("엔(JPY)", "${15000 * (index + 1)}"),
                    CurrencyBalance("원(KRW)", "${132000 * (index + 1)}"),                    CurrencyBalance("엔(JPY)", "${15000 * (index + 1)}"),
                    CurrencyBalance("원(KRW)", "${132000 * (index + 1)}"),                    CurrencyBalance("엔(JPY)", "${15000 * (index + 1)}"),
                    CurrencyBalance("원(KRW)", "${132000 * (index + 1)}"),                    CurrencyBalance("엔(JPY)", "${15000 * (index + 1)}"),
                    CurrencyBalance("원(KRW)", "${132000 * (index + 1)}"),                    CurrencyBalance("엔(JPY)", "${15000 * (index + 1)}"),
                    CurrencyBalance("원(KRW)", "${132000 * (index + 1)}"),
                    CurrencyBalance("엔(JPY)", "${15000 * (index + 1)}"),
                    CurrencyBalance("원(KRW)", "${132000 * (index + 1)}"),
                    CurrencyBalance("엔(JPY)", "${15000 * (index + 1)}"),
                    CurrencyBalance("원(KRW)", "${132000 * (index + 1)}"),
                    CurrencyBalance("엔(JPY)", "${15000 * (index + 1)}"),
                    CurrencyBalance("원(KRW)", "${132000 * (index + 1)}"),                    CurrencyBalance("엔(JPY)", "${15000 * (index + 1)}"),
                    CurrencyBalance("원(KRW)", "${132000 * (index + 1)}"),                    CurrencyBalance("엔(JPY)", "${15000 * (index + 1)}"),
                    CurrencyBalance("원(KRW)", "${132000 * (index + 1)}"),                    CurrencyBalance("엔(JPY)", "${15000 * (index + 1)}"),
                    CurrencyBalance("원(KRW)", "${132000 * (index + 1)}"),                    CurrencyBalance("엔(JPY)", "${15000 * (index + 1)}"),
                    CurrencyBalance("원(KRW)", "${132000 * (index + 1)}"),
                )
            )
        }
        _uiState.update { it.copy(moyeoBoxes = boxes) }
    }
}