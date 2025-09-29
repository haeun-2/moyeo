package com.d108.moyeo.domain.usecase.box

import com.d108.moyeo.domain.model.box.BoxDetail
import com.d108.moyeo.domain.model.box.BoxMember
import com.d108.moyeo.domain.repository.BoxMemberRepository
import javax.inject.Inject

class GetBoxMembersUseCase @Inject constructor(
    private val boxMemberRepository: BoxMemberRepository
) {
    suspend operator fun invoke(boxId: Long): Result<List<BoxMember>> {
        return boxMemberRepository.getBoxMembers(boxId)
    }
}