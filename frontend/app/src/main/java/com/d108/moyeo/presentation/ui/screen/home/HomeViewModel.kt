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
import com.d108.moyeo.domain.usecase.box.GetGroupBoxesUseCase
import com.d108.moyeo.domain.usecase.box.GetPersonalBoxUseCase
import com.d108.moyeo.domain.repository.AuthRepository
import com.d108.moyeo.presentation.theme.boxAvailableColors
import com.d108.moyeo.presentation.ui.screen.home.transfer.CurrencyData
import com.d108.moyeo.util.textColorUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

/**
 * 서버와 로컬(UserDataManager)에서 그룹 박스 데이터를 가져와 조합한 후,
 * 앱 전역 상태인 `BoxStore`와 홈 화면의 UI 상태(`_uiState`)를 모두 업데이트
 * 이 함수는 앱 데이터 로딩의 핵심적인 공급자 역할
 *
 * 데이터 조합 순서:
 * 1. `getGroupBoxesUseCase`를 통해 서버에서 원본 `Box` 리스트를 가져옴
 * 2. 각 `Box`에 대해 `UserDataManager`에서 커스텀 색상, 즐겨찾기 정보를 비동기적으로 조회
 * 3. '로컬 값 ?: 서버/기본 값' 패턴을 사용해 최종 데이터를 결정
 * 4. 모든 정보가 포함된 '완전체' `BoxStoreUiState` 리스트를 생성하여 `BoxStore`를 업데이트
 * 5. `HomeScreen`에 필요한 `GroupBox` 모델로 변환하여 `_uiState`를 업데이트
 */
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

    private val _uiState = MutableStateFlow(  // 홈뷰모델의 uiState
        HomeUiState(
            wallet = WalletSummary(
                title = "",
                bg = boxAvailableColors[1],
                balances = emptyList(),
                id = -1L // 서버 응답 전 임시 데이터
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
        // 첫 로딩
        refresh()
    }


    /**
     * 개인 지갑과 그룹 박스 목록 전체를 새로고침하는 메인 함수.
     */
    fun refresh() = viewModelScope.launch {
        _uiState.update { it.copy(isRefreshing = true) }

        val personal: Job = loadPersonal()
        val group: Job = loadGroups()
        joinAll(personal, group)

        _uiState.update { it.copy(isRefreshing = false) }
    }


    /**
     * 개인 지갑(Personal Box) 정보를 로드하고 UI 상태를 업데이트
     * 서버(`getPersonalBox`)와 로컬(`userDataManager`)에서 데이터를 조합하여 최종 `WalletSummary`를 만듦
     */
    private fun loadPersonal() = viewModelScope.launch {
        getPersonalBox()
            .onSuccess { serverBox -> // 성공 시, 상자 안의 내용물(Box)을 꺼냄
                val localPersonalColorInt = userDataManager.walletColorFlow.first()  // 로컬에 저장된 커스텀 색상을 가져옴
                val finalColor = localPersonalColorInt?.let { Color(it) } ?: _uiState.value.wallet.bg  // 로컬 색상이 있으면 사용하고, 없으면 기존 색상(또는 기본값)을 사용
                val finalWalletSummary = mapPersonalBoxToWalletSummary(serverBox).copy(
                    // 최종 색상을 조합하여 UI 모델 생성
                    title = serverBox.name, // 닉네임은 항상 서버 데이터
                    bg = finalColor       // 색상은 로컬 데이터 우선
                )

                _uiState.update {
                    it.copy(wallet = finalWalletSummary)
                }

                // 개인 지갑의 통화 목록 정보 저장 (다른 화면에서 사용될 수 있음)
                val currencies = serverBox.balances
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

    /**
     * 그룹 박스(Group Box) 목록을 로드하고 `BoxStore`와 UI 상태를 업데이트
     * 서버 데이터와 로컬 데이터를 조합하여 모든 화면에서 사용할 '완전체' `BoxStoreUiState` 리스트를 생성
     */
    private fun loadGroups(): Job =  viewModelScope.launch {
        getGroupBoxes(size = 30)
            .onSuccess { serverBoxList ->  // 서버에서 받은 리스트를
                val finalUiStateList = coroutineScope {
                    serverBoxList.map { serverBox ->  // 각각의 요소에 대하여 기본 정보를 추출
                        async {
                            val id = serverBox.id
                            val name = serverBox.name
                            val isBookmarked = serverBox.isBookmarked
                            val balances = serverBox.balances
                            val bgInt = userDataManager.getGroupColor(id)
                            val bg =  // 3. '로컬 값 ?: 서버/기본 값' 패턴으로 최종 데이터 결정
                                if (bgInt != null)
                                    Color( bgInt)
                                else
                                    colorFromId(id)
                            val textColor = textColorUtil(bg)
                            val type = serverBox.type  // 개인 또는 모임
                            val repr = balances.maxByOrNull {  // 대표금액
                                if (it.currency == "KRW") Double.MAX_VALUE else it.balance
                            } ?: balances.maxByOrNull { it.balance }
                            val amountText = if (repr == null) "잔액 없음" else formatAmount(repr.currency, repr.balance)

                            // 모든 정보를 조합
                            BoxStoreUiState(
                                id = id,
                                title = name,
                                isBookmarked = isBookmarked,
                                bg = bg,
                                textColor = textColor,
                                amount = amountText,
                                balances = balances,
                                type = type,
                            )
                        }
                    }.awaitAll()
                }

                boxStore.setUiStates(finalUiStateList)  // BoxStore에 리스트 저장
                _uiState.update { it.copy(groups = finalUiStateList.map { it.toGroupBox() }) }  // 홈 화면 UI를 위해서는 GroupBox 모델로 변환하여 저장
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
            balances = balances,
            id = box.id
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

    private fun colorFromId(id: Long): Color {  // 임의로 박스 색깔을 저장하는 거 같고..
        val base = abs(id.hashCode())
        // 그러면 여기에서 데이터스토어에 저장하는 게 좋을 듯??
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

    fun onWalletEditConfirm(newName: String, newColor: Color) {
        // 1. 현재 지갑 상태(ID, 이전 값 등)를 가져옵니다.
        val wallet = _uiState.value.wallet
        val walletId = wallet.id // WalletSummary에 id가 있어야 합니다. 없다면 _boxUiStates에서 찾아야 합니다.
        val oldName = wallet.title
        val oldColor = wallet.bg

        _uiState.update {
            it.copy(
                wallet = it.wallet.copy(title = newName, bg = newColor),
                showWalletEditSheet = false
            )
        }


        // 2. '낙관적 업데이트'로 UI를 즉시 변경합니다.
        boxStore.patchBox(id = walletId, newName = newName, newBg = newColor)

        // 3. 백그라운드에서 서버에 업데이트를 요청합니다.
        viewModelScope.launch {
            userDataManager.saveWalletColor(newColor.toArgb()) // 로컬 색상 저장
//            updateWalletNameUseCase(walletId, newName) // TODO: 서버에 이름 변경 요청하는 API 확보
//                .onFailure {
//                    // 4. 실패 시, 롤백합니다.
//                    boxStore.patchBox(id = walletId, newName = oldName)
//                    // 사용자에게 에러 알림
//                }
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
        val oldGroup = boxStore.boxUiStates.value.find { it.id == id } ?: return
        val oldName = oldGroup.title
        val oldColor = oldGroup.bg

        _uiState.update { state ->
            state.copy(
                groups = state.groups.map { group ->
                    if (group.id == id) group.copy(title = newName, bg = newColor)
                    else group
                },
                editingGroupId = null,
                showGroupEditSheet = false
            )
        }

        boxStore.patchBox(id = id, newName = newName, newBg = newColor)

        // 2. 백그라운드에서 서버에 업데이트를 요청합니다.
        viewModelScope.launch {
            userDataManager.saveGroupColor(id, newColor.toArgb()) // 로컬 색상 저장
//            updateGroupNameUseCase(id, newName) // 서버에 이름 변경 요청  TODO: 서버에 이름 바꾸는 API 확보
//                .onFailure {
//                    // 4. 실패 시, 롤백합니다.
//                    boxStore.patchBox(id = id, newName = oldName)
//                    // 사용자에게 에러 알림
//                }
        }
    }

    fun onTransferClicked(currencyId: String) {
        viewModelScope.launch { _navigationEvent.emit(HomeNavigationEvent.NavigateToTransfer(currencyId)) }
    }

    fun onDepositClick(boxId: Long) {
        viewModelScope.launch { _navigationEvent.emit(HomeNavigationEvent.NavigateToDeposit(boxId)) }
    }

    private fun BoxStoreUiState.toGroupBox(): GroupBox = GroupBox(
        id = this.id,
        title = this.title,
        amount = this.amount,
        bg = this.bg,
        isBookmarked = this.isBookmarked
    )
}
