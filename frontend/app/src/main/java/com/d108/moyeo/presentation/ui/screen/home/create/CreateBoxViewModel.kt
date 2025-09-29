package com.d108.moyeo.presentation.ui.screen.home.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.usecase.box.CreateGroupBoxUseCase
import com.d108.moyeo.domain.usecase.box.CreateInviteLinkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CreateBoxEvent {
    data class NavigateResult(val boxId: Long) : CreateBoxEvent
    data class ShowError(val message: String) : CreateBoxEvent
}

@HiltViewModel
class CreateBoxViewModel @Inject constructor(
    private val createGroupBox: CreateGroupBoxUseCase,
    private val createInviteLink: CreateInviteLinkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateBoxUiState())
    val uiState: StateFlow<CreateBoxUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CreateBoxEvent>()
    val events = _events.asSharedFlow()

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, error = null) }
    }

    fun onConfirmClick() {
        val name = uiState.value.name.trim()
        if (name.isEmpty()) {
            viewModelScope.launch { _events.emit(CreateBoxEvent.ShowError("박스 이름을 입력해주세요")) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // 박스 생성
            createGroupBox(name)
                .onSuccess { id ->

                    // 초대 링크 생성
                    createInviteLink(id)
                        .onSuccess { response ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    createdBoxId = id,
                                    inviteLink = response.inviteLink,
                                    inviteCode = response.inviteCode,
                                    expiresAt = response.expiresAt,
                                    currentStep = CreateBoxStep.RESULT
                                )
                            }
                            _events.emit(CreateBoxEvent.NavigateResult(id))
                        }
                        .onFailure { e ->
                            _uiState.update { it.copy(isLoading = false, error = e.message) }
                            _events.emit(CreateBoxEvent.ShowError("박스 생성에 실패했습니다"))
                        }
                }
        }
    }

    fun moveToResult(boxId: Long) {
        _uiState.update { it.copy(createdBoxId = boxId, currentStep = CreateBoxStep.RESULT) }
    }
}
