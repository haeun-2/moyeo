package com.d108.moyeo.presentation.ui.screen.home

import androidx.compose.ui.graphics.Color
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

data class CurrencyBalance(val label: String, val value: String)

data class GroupBox(
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
    object NavigateToMyWallet : HomeNavigationEvent()
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
                CurrencyBalance("한국 원", "120,000 KRW"),
                CurrencyBalance("미국 달러", "20 USD"),
                CurrencyBalance("일본 엔", "400 JPY"),
                CurrencyBalance("영국 파운드", "30 GBP"),
                CurrencyBalance("유럽 유로", "15 EUR"), // 스크롤 테스트를 위해 추가
                CurrencyBalance("중국 위안", "100 CNY")  // 스크롤 테스트를 위해 추가
            )
        )
        val initialGroups = listOf(
            GroupBox("상훈 풍헌 동찬 일본 여행", "50,000 JPY", pink), // 연한 핑크
            GroupBox("미국 도대체 언제 감", "1,500 USD", brown),    // 브라운
            GroupBox("오아시스", "1,000 GBP", purple),               // 라일락
            GroupBox("유럽 갈끄니까", "2,000 EUR", Color.Cyan),     // 스크롤 테스트를 위해 추가
            GroupBox("중국 출장비", "5,000 CNY", Color.Yellow)   // 스크롤 테스트를 위해 추가
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

    fun onWalletCurrencyClick() {
        viewModelScope.launch {
            _navigationEvent.emit(HomeNavigationEvent.NavigateToMyWallet)
        }
    }
}