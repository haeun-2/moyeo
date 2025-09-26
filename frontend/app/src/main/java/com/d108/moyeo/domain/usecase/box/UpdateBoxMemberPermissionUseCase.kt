package com.d108.moyeo.domain.usecase.box

import com.d108.moyeo.domain.model.box.UpdatePermissionRequest
import com.d108.moyeo.domain.repository.BoxMemberRepository
import javax.inject.Inject

class UpdateBoxMemberPermissionUseCase @Inject constructor(
    private val boxMemberRepository: BoxMemberRepository
) {
    suspend operator fun invoke(boxId: Long, requestList: List<UpdatePermissionRequest>): Result<Unit> {
        return boxMemberRepository.updateBoxMemberPermission(boxId, requestList)
    }
}