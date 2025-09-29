package com.d108.moyeo.domain.usecase.box

import com.d108.moyeo.domain.model.box.InviteLinkResult
import com.d108.moyeo.domain.repository.BoxRepository
import javax.inject.Inject

class CreateInviteLinkUseCase @Inject constructor(
    private val repository: BoxRepository
) {
    suspend operator fun invoke(boxId: Long): Result<InviteLinkResult> {
        return repository.createInviteLink(boxId)
    }
}
