package com.d108.moyeo.presentation.ui.screen.home.box.member

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.moyeo.domain.model.box.UpdatePermissionRequest
import com.d108.moyeo.domain.repository.BoxMemberRepository
import com.d108.moyeo.domain.usecase.auth.GetMeUseCase
import com.github.mikephil.charting.utils.Utils.init
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PermissionType {
    TRANSFER, PAYMENT, EXCHANGE
}

sealed class MemberNavEvent {
    object NavigateBack : MemberNavEvent()
}

@HiltViewModel
class MemberViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: BoxMemberRepository,
    private val getMeUseCase: GetMeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemberUiState())
    val uiState: StateFlow<MemberUiState> = _uiState

    private val _navigationEvent = MutableSharedFlow<MemberNavEvent>()
    val navigationEvent: SharedFlow<MemberNavEvent> = _navigationEvent

    private val boxId: Long = savedStateHandle.get<Long>("boxId") ?: -1L
    private val myName: String = ""

    init {
        load(boxId)
    }

    private fun load(boxId: Long) {
        if (boxId == -1L) {
            _uiState.update { it.copy(error = "잘못된 접근입니다.") }
            return
        }

        viewModelScope.launch {
            val membersResultDeferred = async { repository.getBoxMembers(boxId) }
            val meResultDeferred = async { getMeUseCase() }

            val membersResult = membersResultDeferred.await()
            val meResult = meResultDeferred.await()
            if (membersResult.isSuccess && meResult.isSuccess) {
                val domainMembers = membersResult.getOrThrow()
                val myName = meResult.getOrThrow().name

                val currentUserIsOwner = domainMembers.any { it.name == myName && it.permission.isOwner }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        members = domainMembers.map { member -> BoxMemberUi(member = member, expanded = true) },
                        isCurrentUserOwner = currentUserIsOwner
                    )
                }
            } else {
                // 둘 중 하나라도 실패했다면 에러 상태로 변경합니다.
                val errorMessage = membersResult.exceptionOrNull()?.message
                    ?: meResult.exceptionOrNull()?.message
                    ?: "알 수 없는 오류"
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
            }

        }
    }

    fun onPermissionChange(memberId: Long, type: PermissionType) {
        val currentState = _uiState.value
        val originalMembers = currentState.members

        // 1. UI 즉시 업데이트
        val updatedMembers = originalMembers.map { uiModel ->
            if (uiModel.member.id == memberId) {
                val oldPermission = uiModel.member.permission
                val newPermission = when (type) {
                    PermissionType.TRANSFER -> oldPermission.copy(canTransfer = !oldPermission.canTransfer)
                    PermissionType.PAYMENT -> oldPermission.copy(canPayment = !oldPermission.canPayment)
                    PermissionType.EXCHANGE -> oldPermission.copy(canExchange = !oldPermission.canExchange)
                }
                uiModel.copy(member = uiModel.member.copy(permission = newPermission))
            } else {
                uiModel
            }
        }
        _uiState.update { it.copy(members = updatedMembers) }

        // API 콜
        viewModelScope.launch {
            val memberToUpdate = updatedMembers.find { it.member.id == memberId }?.member
            if (memberToUpdate == null) {
                // 만약 업데이트할 멤버를 못찾으면 원래대로 롤백 (예외 케이스)
                _uiState.update { it.copy(members = originalMembers) }
                return@launch
            }

            val request = UpdatePermissionRequest(
                boxMemberId = memberToUpdate.id,
                canTransfer = memberToUpdate.permission.canTransfer,
                canPayment = memberToUpdate.permission.canPayment,
                canExchange = memberToUpdate.permission.canExchange
            )

            repository.updateBoxMemberPermission(boxId, listOf(request))
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            members = originalMembers,
                            error = "권한 변경에 실패했습니다: ${error.message}"
                        )
                    }
                }
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _navigationEvent.emit(MemberNavEvent.NavigateBack)
        }
    }
}
