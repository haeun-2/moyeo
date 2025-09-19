package com.d108.moyeo.presentation.ui.screen.home

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.core.BoxStore
import com.d108.moyeo.data.local.UserDataManager
import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.usecase.box.GetGroupBoxesUseCase
import com.d108.moyeo.domain.usecase.box.GetPersonalBoxUseCase
import com.d108.moyeo.domain.repository.AuthRepository
import com.d108.moyeo.presentation.theme.boxAvailableColors
import com.d108.moyeo.presentation.ui.screen.home.transfer.CurrencyData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getPersonalBox: GetPersonalBoxUseCase,
    private val getGroupBoxes: GetGroupBoxesUseCase,
    private val userDataManager: UserDataManager,
    private val boxStore: BoxStore
) : ViewModel() {

    private var didHandleFirstResume: Boolean = false

    fun onResumed() {
        if (didHandleFirstResume) {
            refresh()
        } else {
            didHandleFirstResume = true
        }
    }

    private val _uiState = MutableStateFlow(
        HomeUiState(
            wallet = WalletSummary(
                title = "",
                bg = boxAvailableColors[1],
                balances = emptyList()
            ),
            groups = emptyList(),
            showWalletEditSheet = false,
            isRefreshing = false
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

        // 색상 로딩
        viewModelScope.launch {
            userDataManager.walletColorFlow.collect { savedColor ->
                if (savedColor != null) {
                    _uiState.update { state ->
                        state.copy(wallet = state.wallet.copy(bg = Color(savedColor)))
                    }
                }
            }
        }

        // 박스 이름 로딩
        viewModelScope.launch {
            userDataManager.walletNameFlow.collect { savedName ->
                if (!savedName.isNullOrBlank()) {
                    _uiState.update { s -> s.copy(wallet = s.wallet.copy(title = savedName)) }
                }
            }
        }
        
        // 첫 로딩
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        _uiState.update { it.copy(isRefreshing = true) }

        val personal: Job = loadPersonal()
        val group: Job = loadGroups()
        joinAll(personal, group)

        _uiState.update { it.copy(isRefreshing = false) }
    }

    // refresh 단계에서 개인 정보를 불러올 때 불러온 정보를 BoxStore 에 저장.
    private fun loadPersonal() = viewModelScope.launch {
        getPersonalBox()
            .onSuccess { box -> // 성공 시, 상자 안의 내용물(Box)을 꺼냅니다.
                _uiState.update {
                    it.copy(wallet = mapPersonalBoxToWalletSummary(box))
                }
                val currencies = box.balances
                    .filter { it.balance != 0.0 }
                    .sortedByDescending { it.balance } // TODO: 정렬 삭제
                    .map {
                        CurrencyData(
                            name = currencyLabel(it.currency),
                            code = it.currency
                        )
                    }
                boxStore.setPersonalCurrencies(currencies)
            }
            .onFailure { e ->
                Log.e("HomeVM", "getPersonalBox failed", e)
            }
    }

    private fun loadGroups(): Job =  viewModelScope.launch {
        getGroupBoxes(size = 30)
            .onSuccess { list ->
                val base: List<GroupBox> = list.map(::mapGroupBoxToUi)

                val mapped = kotlinx.coroutines.coroutineScope {
                    base.map { groupBox ->
                        async {
                            val id = groupBox.id
                            val savedName  = userDataManager.getGroupName(id)
                            val savedColor = userDataManager.getGroupColor(id)?.let { Color(it) }
                            val patchedBg = savedColor ?: groupBox.bg
                            val patchedName = savedName ?: groupBox.title
                            groupBox.copy(title = patchedName, bg = patchedBg)
                        }
                    }.awaitAll()
                }

                _uiState.update { it.copy(groups = mapped) }
                boxStore.setGroupBoxes(mapped)
            }
            .onFailure { e -> Log.e("HomeViewModel", "loadGroups failed", e) }
    }

    // ---------- 매핑 ----------

    private fun mapPersonalBoxToWalletSummary(box: Box): WalletSummary {
        val balances = box.balances
            .filter { it.balance != 0.0 } // 0이 아닌 통화만 출력
            .sortedByDescending { it.balance } // TODO: 정렬 삭제
            .map {
                CurrencyBalance(
                    label = currencyLabel(it.currency),
                    value = formatAmount(it.currency, it.balance),
                    code = it.currency)
            }
        val title = _uiState.value.wallet.title
            .takeIf { it.isNotBlank() }?: box.name

        return WalletSummary(
            title = title,
            bg = _uiState.value.wallet.bg,
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
            id = box.id,
            title = box.name,
            amount = amountText,
            bg = colorFromId(box.id)
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
        return boxAvailableColors[base % boxAvailableColors.size]
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
                    bg = newColor
                ),
                showWalletEditSheet = false
            )
        }
        viewModelScope.launch {
            userDataManager.saveWalletColor(newColor.toArgb())
            userDataManager.saveWalletName(newName)
        }
    }

    fun onWalletCurrencyClick() {
        viewModelScope.launch {
            _navigationEvent.emit(
                HomeNavigationEvent.NavigateToMyWallet
            )
        }
    }

    fun onGroupBoxClick(boxId: Long) {
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

    fun onGroupMoreClick(boxId: Long) {
        _uiState.update { it.copy(editingGroupId = boxId, showGroupEditSheet = true) }
    }

    fun onGroupEditDismiss() {
        _uiState.update { it.copy(editingGroupId = null, showGroupEditSheet = false) }
    }

    // 박스 정보를 수정했을 때 해당 정보를 BoxStore 에 저장
    fun onGroupEditConfirm(newName: String, newColor: Color) {
        val id = _uiState.value.editingGroupId ?: return
        _uiState.update { state ->
            state.copy(
                groups = state.groups.map { group ->
                    if (group.id == id) group.copy(
                        title = newName,
                        bg = newColor
                    ) else group
                },
                editingGroupId = null,
                showGroupEditSheet = false
            )
        }
        viewModelScope.launch {
            userDataManager.saveGroupName(id, newName)
            userDataManager.saveGroupColor(id, newColor.toArgb())
        }
        boxStore.patchBox(
            id = id,
            newName = newName,
            newBg = newColor
        )
    }

    fun onTransferClicked(currencyId: String) {
        viewModelScope.launch { _navigationEvent.emit(HomeNavigationEvent.NavigateToTransfer(currencyId)) }
    }

    fun onDepositClick(boxId: Long) {
        viewModelScope.launch { _navigationEvent.emit(HomeNavigationEvent.NavigateToDeposit(boxId)) }
    }
}
