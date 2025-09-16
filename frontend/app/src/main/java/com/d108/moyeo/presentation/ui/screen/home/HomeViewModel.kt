package com.d108.moyeo.presentation.ui.screen.home

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.usecase.box.GetGroupBoxesUseCase
import com.d108.moyeo.domain.usecase.box.GetPersonalBoxUseCase
import com.d108.moyeo.domain.repository.AuthRepository
import com.d108.moyeo.presentation.theme.surfaceLight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getPersonalBox: GetPersonalBoxUseCase,
    private val getGroupBoxes: GetGroupBoxesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            wallet = WalletSummary(
                title = "",
                color = surfaceLight,
                balances = emptyList()
            ),
            groups = emptyList(),
            showWalletEditSheet = false
        )
    )
    val uiState = _uiState.asStateFlow()

    // 화면 이동 이벤트
    private val _navigationEvent = MutableSharedFlow<HomeNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        // 토큰 로깅 (기존 흐름 유지)
        viewModelScope.launch {
            authRepository.accessToken.collect {
                Log.d("TOKEN_CHECK", "현재 AccessToken: $it")
            }
        }
        viewModelScope.launch {
            authRepository.refreshToken.collect {
                Log.d("TOKEN_CHECK", "현재 RefreshToken: $it")
            }
        }

        // 첫 로딩
        refresh()
    }

    fun refresh() {
        loadPersonal()
        loadGroups()
    }

    private fun loadPersonal() = viewModelScope.launch {
        runCatching { getPersonalBox() }
            .onSuccess {
                box -> _uiState.update {
                    it.copy(wallet = mapPersonalBoxToWalletSummary(box))
                }
            }
            .onFailure {
                e -> Log.e("HomeVM", "getPersonalBox failed", e)
            }
    }

    private fun loadGroups() {
        viewModelScope.launch {
            try {
                val list = getGroupBoxes(page = 0, size = 30)
                val mapped = list.map(::mapGroupBoxToUi)
                _uiState.update { it.copy(groups = mapped) }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "loadGroups failed", e)
            }
        }
    }

    // ---------- 매핑 ----------

    private fun mapPersonalBoxToWalletSummary(box: Box): WalletSummary {
        val balances = box.balances
            .filter { it.balance != 0.0 } // 0이 아닌 통화만 출력
            .sortedByDescending { it.balance } // 보유 많은 순
            .map {
                CurrencyBalance(
                    label = currencyLabel(it.currency),
                    value = formatAmount(it.currency, it.balance),
                    code = it.currency)
            }

        return WalletSummary(
            title = box.name,
            color = colorFromId(box.id),
            balances = balances
        )
    }

    private fun mapGroupBoxToUi(box: Box): GroupBox {
        val repr = box.balances
            .maxByOrNull {
                if (it.currency == "KRW") Double.MAX_VALUE else it.balance
            }
            ?: box.balances.maxByOrNull { it.balance }

        val amountText = if (repr == null) "잔액 없음" else formatAmount(repr.currency, repr.balance)

        return GroupBox(
            id = box.id.toString(),
            title = box.name,
            amount = amountText,
            bg = colorFromId(box.id).copy(alpha = 0.25f)
        )
    }

    // ---------- 유틸 ----------

    private fun currencyLabel(code: String): String = when (code) {
        "KRW" -> "한국 원"
        "USD" -> "미국 달러"
        "JPY" -> "일본 엔"
        "EUR" -> "유럽 유로"
        "GBP" -> "영국 파운드"
        "CNY" -> "중국 위안"
        "CHF" -> "스위스 프랑"
        "CAD" -> "캐나다 달러"
        else  -> code
    }

    private fun formatAmount(code: String, amount: Double): String {
        val rounded = if (amount % 1.0 == 0.0) amount.toLong().toString() else String.format("%.4f", amount)
        return "$rounded $code"
    }

    private fun colorFromId(id: Long): Color {
        val base = abs(id.hashCode())
        val palette = listOf(
            Color(0xFF8BC34A), Color(0xFF4CAF50), Color(0xFF03A9F4), Color(0xFF00BCD4),
            Color(0xFFCDDC39), Color(0xFFFFC107), Color(0xFFFF9800), Color(0xFF9C27B0),
            Color(0xFFE91E63), Color(0xFF3F51B5)
        )
        return palette[base % palette.size]
    }

    // ---------- 네비게이션 ----------

    fun onWalletTitleClick() {
        viewModelScope.launch {
            _navigationEvent.emit(
                HomeNavigationEvent.NavigateToMyWallet
            )
        }
    }

    fun onWalletMoreClick() {
        _uiState.update {
            it.copy(showWalletEditSheet = true)
        }
    }
    fun onWalletEditDismiss() {
        _uiState.update {
            it.copy(showWalletEditSheet = false)
        }
    }

    fun onWalletEditConfirm(
        newName: String,
        newColor: Color
    ) {
        _uiState.update { current ->
            current.copy(
                wallet = current.wallet.copy(
                    title = newName,
                    color = newColor
                ),
                showWalletEditSheet = false
            )
        }
    }

    fun onWalletCurrencyClick() {
        viewModelScope.launch {
            _navigationEvent.emit(
                HomeNavigationEvent.NavigateToMyWallet
            )
        }
    }

    fun onGroupBoxClick(boxId: String) {
        viewModelScope.launch {
            val clicked = _uiState.value.groups.find { it.id == boxId }
            if (clicked != null) {
                _navigationEvent.emit(
                    HomeNavigationEvent.NavigateToMyBox(
                        boxId, clicked.bg.toArgb()
                    )
                )
            }
        }
    }

    fun onDepositClick(boxId: String) {
        viewModelScope.launch { _navigationEvent.emit(HomeNavigationEvent.NavigateToCollecting(boxId)) }
    }

    fun onTransferClicked(currencyId: String) {
        viewModelScope.launch { _navigationEvent.emit(HomeNavigationEvent.NavigateToSending(currencyId)) }
    }
}
