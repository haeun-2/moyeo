package com.d108.moyeo.presentation.ui.screen.home

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.presentation.theme.brown
import com.d108.moyeo.presentation.theme.pink
import com.d108.moyeo.presentation.theme.purple
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// TODO: 이 데이터 클래스들은 domain/model 패키지로 이동해야 합니다.
data class WalletSummary(
    val title: String,
    val color: Color,
    val balances: List<CurrencyBalance>
)

data class CurrencyBalance(val label: String, val value: String, val code: String)

data class GroupBox(
    val id: String,  // 그룹 박스 구분을 위한 아이디 추가
    val title: String,
    val amount: String,
    val bg: Color
)

// HomeScreen의 모든 UI 상태를 담는 데이터 클래스
data class HomeUiState(
    val wallet: WalletSummary,
    val groups: List<GroupBox> = emptyList(),
    val showWalletEditSheet: Boolean = false
)

// 화면 전환
sealed class HomeNavigationEvent {
    // 단순히 이동하고자 하면 object로 선언 가능
    object NavigateToMyWallet : HomeNavigationEvent()

    // 구체적으로 어디로 가야하는지 알고 싶으면 data class로 선언 및 파라미터 전달
    data class NavigateToMyBox(val boxId: String, val bgColor: Int) : HomeNavigationEvent()

    data class NavigateToSending(val currencyId: String) : HomeNavigationEvent() // 이체 화면 이동
}


class HomeViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState>

    // 화면 이동 이벤트를 전달할 SharedFlow
    private val _navigationEvent = MutableSharedFlow<HomeNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        // 샘플 데이터 (이미지와 동일한 분위기/텍스트)
        val initialWallet = WalletSummary(
            title = "일론머스크 딱 대",
            color = Color.Blue, // 임시 대표 색상
            balances = listOf(
                CurrencyBalance("한국 원", "120,000 KRW", "KRW"),
                CurrencyBalance("미국 달러", "20 USD", "USD"),
                CurrencyBalance("일본 엔", "400 JPY", "JPY"),
                CurrencyBalance("영국 파운드", "30 GBP", "GBP"),
                CurrencyBalance("유럽 유로", "15 EUR", "EUR"), // 스크롤 테스트를 위해 추가
                CurrencyBalance("중국 위안", "100 CNY", "CNY")  // 스크롤 테스트를 위해 추가
            )
        )
        val initialGroups = listOf(  // GroupBox의 생성자 변경 및 아이디 추가
            GroupBox("box_1", "상훈 풍헌 동찬 일본 여행", "50,000 JPY", pink), // 연한 핑크
            GroupBox("box_2", "미국 도대체 언제 감", "1,500 USD", brown),    // 브라운
            GroupBox("box_3", "오아시스", "1,000 GBP", purple),               // 라일락
            GroupBox("box_4", "유럽 갈끄니까", "2,000 EUR", Color.Cyan),     // 스크롤 테스트를 위해 추가
            GroupBox("box_5", "중국 출장비", "5,000 CNY", Color.Yellow)   // 스크롤 테스트를 위해 추가
        )

        _uiState = MutableStateFlow(HomeUiState(wallet = initialWallet, groups = initialGroups))
    }

    val uiState = _uiState.asStateFlow()

    fun onWalletTitleClick() {  // 내 지갑 "제목 >" 영역 클릭 시
        viewModelScope.launch {
            _navigationEvent.emit(HomeNavigationEvent.NavigateToMyWallet)
        }
    }

    fun onWalletMoreClick() {
        _uiState.update { it.copy(showWalletEditSheet = true) }
    }

    fun onWalletEditDismiss() {
        _uiState.update { it.copy(showWalletEditSheet = false) }
    }

    fun onWalletEditConfirm(newName: String, newColor: Color) {
        _uiState.update { currentState ->
            val updatedWallet = currentState.wallet.copy(title = newName, color = newColor)
            currentState.copy(wallet = updatedWallet, showWalletEditSheet = false)
        }
    }

    fun onWalletCurrencyClick() {  // 나중에 여기에 파라미터 넣어서 뭘 보이게 할지 해야겠네
        viewModelScope.launch {
            _navigationEvent.emit(HomeNavigationEvent.NavigateToMyWallet)
        }
    }

    // 그룹 박스에서 클릭되었을 때 해당 박스 ID로 이동
    fun onGroupBoxClick(boxId: String) {
        viewModelScope.launch {
            // 클릭된 ID에 해당하는 박스를 찾아 색상 정보도 함께 이벤트에 담아 전송
            val clickedBox = _uiState.value.groups.find { it.id == boxId }
            if (clickedBox != null) {
                // Color 객체를 Int로 변환하여 전달
                _navigationEvent.emit(HomeNavigationEvent.NavigateToMyBox(boxId, clickedBox.bg.toArgb()))
            }
        }
    }


    // 돈 보내는 함수
    fun onTransferClicked(currencyId: String) {
        viewModelScope.launch {
            _navigationEvent.emit(HomeNavigationEvent.NavigateToSending(currencyId))
        }
    }
}