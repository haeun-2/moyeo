// JoinViewModel.kt (전체 교체 예시)
package com.d108.moyeo.presentation.ui.screen.home.join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.data.local.UserDataManager
import com.d108.moyeo.domain.usecase.box.JoinBoxUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val joinBoxUseCase: JoinBoxUseCase,
    private val userDataManager: UserDataManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinUiState())
    val uiState: StateFlow<JoinUiState> = _uiState.asStateFlow()

    // PIN 입력 요청/응답 (UI에서 처리)
    val pinRequestEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val pinResultEvent = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)

    /** 시작: DS에서 생체 사용 여부를 읽고 PIN 단계로 진입(딥링크 코드 있으면 미리 주입) */
    fun startAuthFlow(code: String? = null) = viewModelScope.launch {
        if (!code.isNullOrBlank()) _uiState.update { it.copy(joinCode = code) }

        val enabled = runCatching {
            userDataManager.biometricsPreferenceFlow.first()
        }.getOrElse { false }

        _uiState.update { it.copy(biometricsEnabled = enabled, currentStep = JoinStep.PIN) }
    }

    /** 지문 성공 시 호출: BOX 단계로 전환 */
    fun onBiometricSuccess() {
        _uiState.update { it.copy(currentStep = JoinStep.BOX, errorMessage = null) }
    }

    /** PIN 입력을 요청 (UI가 화면을 띄운 뒤 결과를 submitPinResult로 전달) */
    fun requestPin() {
        pinRequestEvent.tryEmit(Unit)
    }

    /** UI가 PIN 결과 전달 */
    fun submitPinResult(ok: Boolean) = viewModelScope.launch {
        if (ok) {
            _uiState.update { it.copy(currentStep = JoinStep.BOX, errorMessage = null) }
        } else {
            _uiState.update { it.copy(errorMessage = "인증이 취소되었습니다.") }
        }
    }

    /** BOX 화면: 코드 변경 */
    fun onJoinCodeChanged(code: String) {
        _uiState.update { it.copy(joinCode = code) }
    }

    /** BOX 화면: 참여하기 → API 호출 후 FINISHED */
    fun confirmJoin() = viewModelScope.launch {
        val code = uiState.value.joinCode.trim()
        if (code.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "초대 코드를 입력하세요.") }
            return@launch
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        joinBoxUseCase(code)
            .onSuccess { _uiState.update { it.copy(isLoading = false, currentStep = JoinStep.FINISHED) } }
            .onFailure { e ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "박스 합류에 실패했습니다.")
                }
            }
    }
}
