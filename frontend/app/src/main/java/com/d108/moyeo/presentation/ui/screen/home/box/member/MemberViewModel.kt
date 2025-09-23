package com.d108.moyeo.presentation.ui.screen.home.box.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    // TODO: private val repository: BoxMemberRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemberUiState())
    val uiState: StateFlow<MemberUiState> = _uiState

    fun load(boxId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // TODO: repository.getMembers(boxId)
                val demo = listOf(
                    BoxMemberUi(1, "김동찬", true,  MemberCapabilities(false, false, true)),
                    BoxMemberUi(2, "박상훈", false, MemberCapabilities(false, false, false)),
                    BoxMemberUi(3, "정자빈", false, MemberCapabilities(false, true,  false), expanded = true),
                    BoxMemberUi(4, "김하은", false, MemberCapabilities(false, false, false))
                )
                _uiState.update { it.copy(isLoading = false, members = demo) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = t.message ?: "알 수 없는 오류") }
            }
        }
    }

    fun toggleExpand(id: Long) {
        _uiState.update { state ->
            state.copy(
                members = state.members.map { m ->
                    if (m.id == id) m.copy(expanded = !m.expanded) else m
                }
            )
        }
    }
}
