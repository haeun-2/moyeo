package com.d108.moyeo.presentation.ui.screen.home

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.core.BoxStore
import com.d108.moyeo.core.BoxStoreUiState
import com.d108.moyeo.data.local.UserDataManager
import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.model.box.BoxType
import com.d108.moyeo.domain.usecase.box.AddBookmarkUseCase
import com.d108.moyeo.domain.usecase.box.DeleteBookmarkUseCase
import com.d108.moyeo.domain.usecase.box.GetGroupBoxesUseCase
import com.d108.moyeo.domain.usecase.box.GetPersonalBoxUseCase
import com.d108.moyeo.domain.repository.AuthRepository
import com.d108.moyeo.presentation.theme.boxAvailableColors
import com.d108.moyeo.presentation.ui.screen.home.transfer.CurrencyData
import com.d108.moyeo.util.CurrencyUtils.getCurrencyName
import com.d108.moyeo.util.textColorUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

/**
 * 홈 화면의 모든 UI 상태와 비즈니스 로직을 총괄하는 컨트롤 타워.
 *
 * 이 ViewModel은 앱의 핵심 데이터 공급자(Provider) 역할을 수행합니다. 서버 API와 로컬 DataStore에서
 * 데이터를 가져와 조합한 후, 앱 전역 상태 저장소인 `BoxStore`와 홈 화면 자체의 UI 상태(`_uiState`)를
 * 모두 업데이트할 책임을 가집니다. 또한 사용자의 모든 상호작용(수정, 클릭 등)을 처리합니다.
 *
 * @see BoxStore 앱 전역 박스 목록을 저장하는 중앙 캐시.
 * @see UserDataManager 사용자의 커스텀 설정(색상, 즐겨찾기)을 저장하는 로컬 저장소.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getPersonalBoxUseCase: GetPersonalBoxUseCase,
    private val getGroupBoxesUseCase: GetGroupBoxesUseCase,
    private val userDataManager: UserDataManager,
    private val boxStore: BoxStore,
    private val addBookmarkUseCase: AddBookmarkUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase
) : ViewModel() {

    private var didHandleFirstResume: Boolean = false

    private val _uiState = MutableStateFlow(
        HomeUiState(
            wallet = WalletSummary(
                id = -1L,
                title = "로딩 중...",
                bg = boxAvailableColors[1],
                balances = emptyList()
            ),
            groups = emptyList(),
            showWalletEditSheet = false,
            isRefreshing = false
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HomeNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        // ... (토큰 로깅은 그대로)
        refresh()

        viewModelScope.launch {
            userDataManager.userNameFlow.collect { name ->
                _uiState.update { it.copy(userName = name ?: "사용자") }
            }
        }
    }

    /**
     * 화면이 다시 활성화될 때 데이터를 새로고침하기 위한 함수.
     */
    fun onResumed() {
        if (didHandleFirstResume) {
            refresh()
        } else {
            didHandleFirstResume = true
        }
    }

    /**
     * 개인 지갑과 그룹 박스 목록 전체를 새로고침하고, 하나의 리스트로 통합하여 BoxStore와 UI에 반영하는 메인 함수.
     */
    fun refresh() = viewModelScope.launch {
        _uiState.update { it.copy(isRefreshing = true) }

        // 1. async를 사용하여 개인 박스와 그룹 박스를 '동시에' 요청
        val personalBoxResultDeferred = async { getPersonalBoxUseCase() }
        val groupBoxesResultDeferred = async { getGroupBoxesUseCase(size = 30) }

        val personalResult = personalBoxResultDeferred.await()
        val groupResult = groupBoxesResultDeferred.await()

        // 2. 두 요청의 성공적인 결과를 하나의 리스트로 합침
        val allServerBoxes = mutableListOf<Box>()
        personalResult.onSuccess { allServerBoxes.add(it) }
        groupResult.onSuccess { allServerBoxes.addAll(it) }

        // 3. 합쳐진 전체 리스트를 기준으로 '완전체' UI State 리스트로 변환 (로컬 데이터와 조합)
        val finalUiStateList = coroutineScope {
            allServerBoxes.map { serverBox ->
                async {
                    serverBox.toBoxStoreUiState()
                }
            }.awaitAll()
        }

        // 4. 최종적으로 통합된 리스트를 BoxStore에 저장
        boxStore.setUiStates(finalUiStateList)

        // 5. BoxStore의 최신 데이터를 기반으로 홈 화면 UI를 업데이트
        updateUiFromBoxStore()

        // 6. 개인 지갑 통화 목록도 BoxStore에 저장
        personalResult.onSuccess { personalBox ->
            val currencies = personalBox.balances
//                .filter { it.balance != 0.0 }  //TODO: 일단 이거 0원 아닌 것도 나오게 해봄
                .map { CurrencyData(name = getCurrencyName(it.currency), code = it.currency) }
            boxStore.setPersonalCurrencies(currencies)
        }

        _uiState.update { it.copy(isRefreshing = false) }
    }

    /**
     * BoxStore의 최신 데이터를 읽어와 HomeScreen의 UI State를 업데이트합니다.
     */
    private fun updateUiFromBoxStore() {
        val allBoxes = boxStore.boxUiStates.value

        val walletState = allBoxes.find { it.type == BoxType.PERSONAL }
        val groupStates = allBoxes.filter { it.type == BoxType.GROUP }

        _uiState.update { currentState ->
            currentState.copy(
                wallet = walletState?.toWalletSummary() ?: currentState.wallet,
                groups = groupStates.map { it.toGroupBox() }
            )
        }
    }

    fun onToggleBookmark(boxId: Long, currentIsBookmarked: Boolean) {
        val newIsBookmarked = !currentIsBookmarked
        viewModelScope.launch {
            val result = if (newIsBookmarked) {
                addBookmarkUseCase(boxId)
            } else {
                deleteBookmarkUseCase(boxId)
            }
            result
                .onSuccess {
                    if (newIsBookmarked) {
                        userDataManager.addBookmark(boxId)
                    } else {
                        userDataManager.deleteBookmark(boxId)
                    }
                    refresh()
                }
                .onFailure { error ->
                    Log.e("HomeViewModel", "Bookmark update failed: $error")
                }
        }
    }

    // ---------- 이벤트 핸들러 ----------

    fun onWalletMoreClick() {
        _uiState.update { it.copy(showWalletEditSheet = true) }
    }

    fun onWalletEditDismiss() {
        _uiState.update { it.copy(showWalletEditSheet = false) }
    }

    fun onWalletEditConfirm(newName: String, newColor: Color) {
        val wallet = _uiState.value.wallet
        val walletId = wallet.id
        val oldName = wallet.title

        _uiState.update {
            it.copy(
                wallet = it.wallet.copy(title = newName, bg = newColor),
                showWalletEditSheet = false
            )
        }
        boxStore.patchBox(id = walletId, newName = newName, newBg = newColor)

        viewModelScope.launch {
            userDataManager.saveWalletColor(newColor.toArgb())
            // updateWalletNameUseCase(walletId, newName).onFailure { ... 롤백 로직 ... }
        }
    }

    fun onGroupMoreClick(boxId: Long) {
        _uiState.update { it.copy(editingGroupId = boxId, showGroupEditSheet = true) }
    }

    fun onGroupEditDismiss() {
        _uiState.update { it.copy(editingGroupId = null, showGroupEditSheet = false) }
    }

    fun onGroupEditConfirm(newName: String, newColor: Color) {
        val id = _uiState.value.editingGroupId ?: return
        val oldGroup = _uiState.value.groups.find { it.id == id } ?: return
        val oldName = oldGroup.title

        _uiState.update { state ->
            state.copy(
                groups = state.groups.map { group ->
                    if (group.id == id) group.copy(title = newName, bg = newColor) else group
                },
                editingGroupId = null,
                showGroupEditSheet = false
            )
        }
        boxStore.patchBox(id = id, newName = newName, newBg = newColor)

        viewModelScope.launch {
            userDataManager.saveGroupColor(id, newColor.toArgb())
            // updateGroupNameUseCase(id, newName).onFailure { ... 롤백 로직 ... }
        }
    }

    // ---------- 네비게이션 ----------

    fun onWalletTitleClick() {
        val walletId = _uiState.value.wallet.id
        if (walletId == -1L) return
        viewModelScope.launch {
            _navigationEvent.emit(HomeNavigationEvent.NavigateToMyWallet(walletId, "KRW"))
        }
    }

    fun onWalletCurrencyClick(currency: CurrencyBalance) {
        val walletId = _uiState.value.wallet.id
        if (walletId == -1L) return
        viewModelScope.launch {
            _navigationEvent.emit(HomeNavigationEvent.NavigateToMyWallet(walletId, currency.code))
        }
    }

    fun onGroupBoxClick(boxId: Long) {
        val clicked = _uiState.value.groups.find { it.id == boxId }
        if (clicked != null) {
            viewModelScope.launch {
                _navigationEvent.emit(HomeNavigationEvent.NavigateToMyBox(boxId))
            }
        }
    }

    fun onTransferClicked(currencyId: String) {
        viewModelScope.launch { _navigationEvent.emit(HomeNavigationEvent.NavigateToTransfer(currencyId)) }
    }

    fun onDepositClick(boxId: Long) {
        viewModelScope.launch { _navigationEvent.emit(HomeNavigationEvent.NavigateToDeposit(boxId)) }
    }

    // ---------- 헬퍼 함수 ----------

    private suspend fun Box.toBoxStoreUiState(): BoxStoreUiState {
        val localColor = if (this.type == BoxType.PERSONAL) {
            userDataManager.walletColorFlow.first()
        } else {
            userDataManager.getGroupColor(this.id)
        }
        val finalColor = localColor?.let { Color(it) } ?: colorFromId(this.id)

        val serverIsBookmarked = this.isBookmarked
        val localIsBookmarked = userDataManager.isBookmarked(this.id)
        val finalIsBookmarked = serverIsBookmarked || localIsBookmarked

        val repr = this.balances.maxByOrNull { it.balance }
        val amountText = if (repr == null) "잔액 없음" else formatAmount(repr.currency, repr.balance)

        return BoxStoreUiState(
            id = this.id,
            title = this.name,
            bg = finalColor,
            textColor = textColorUtil(finalColor),
            isBookmarked = finalIsBookmarked,
            amount = amountText,
            balances = this.balances,
            type = this.type
        )
    }

    private fun BoxStoreUiState.toGroupBox(): GroupBox = GroupBox(
        id = this.id,
        title = this.title,
        amount = this.amount,
        bg = this.bg,
        isBookmarked = this.isBookmarked
    )

    private fun BoxStoreUiState.toWalletSummary(): WalletSummary = WalletSummary(
        id = this.id,
        title = this.title,
        bg = this.bg,
        balances = this.balances.map {
            CurrencyBalance(
                label = getCurrencyName(it.currency),
                value = formatAmount(it.currency, it.balance),
                code = it.currency
            )
        }
    )

    private fun mapPersonalBoxToWalletSummary(box: Box): WalletSummary {
        val balances = box.balances
            .filter { it.balance != 0.0 }
            .map {
                CurrencyBalance(
                    label = getCurrencyName(it.currency),
                    value = formatAmount(it.currency, it.balance),
                    code = it.currency)
            }
        return WalletSummary(
            id = box.id,
            title = box.name,
            bg = _uiState.value.wallet.bg,
            balances = balances
        )
    }

    private fun formatAmount(code: String, amount: Double): String {
        val rounded = if (amount % 1.0 == 0.0) amount.toLong().toString() else String.format("%.2f", amount)
        return "$rounded $code"
    }

    private fun colorFromId(id: Long): Color {
        val base = abs(id.hashCode())
        return boxAvailableColors[base % boxAvailableColors.size]
    }
}