package com.d108.moyeo.presentation.ui.screen.home.box.member

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.repository.BoxMemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MemberNavEvent {
    object NavigateBack : MemberNavEvent()
}

@HiltViewModel
class MemberViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: BoxMemberRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemberUiState())
    val uiState: StateFlow<MemberUiState> = _uiState

    private val _navigationEvent = MutableSharedFlow<MemberNavEvent>()
    val navigationEvent: SharedFlow<MemberNavEvent> = _navigationEvent

    private val boxId: Long = savedStateHandle.get<Long>("boxId") ?: -1L

    init {
        load(boxId)
    }

    private fun load(boxId: Long) {
        if (boxId == -1L) {
            _uiState.update { it.copy(error = "잘못된 접근입니다.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getBoxMembers(boxId)
                .onSuccess { domainMembers ->
                    _uiState.update { memberUiState->
                        memberUiState.copy(
                            isLoading = false,
                            members = domainMembers.map { BoxMemberUi(member = it) }
                        )
                    }
                }
                .onFailure { t ->
                    _uiState.update { it.copy(isLoading = false, error = t.message ?: "알 수 없는 오류") }
                }
        }
    }

    fun toggleExpand(id: Long) {
        _uiState.update { state ->
            state.copy(
                members = state.members.map { uiModel ->
                    if (uiModel.member.id == id) {
                        uiModel.copy(expanded = !uiModel.expanded)
                    } else {
                        uiModel
                    }
                }
            )
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _navigationEvent.emit(MemberNavEvent.NavigateBack)
        }
    }
}
