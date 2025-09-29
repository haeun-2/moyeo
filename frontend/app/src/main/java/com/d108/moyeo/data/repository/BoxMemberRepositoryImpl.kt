package com.d108.moyeo.data.repository

import com.d108.moyeo.data.mapper.toDomain
import com.d108.moyeo.data.mapper.toDto
import com.d108.moyeo.data.remote.api.BoxMemberService
import com.d108.moyeo.domain.model.box.BoxMember
import com.d108.moyeo.domain.model.box.UpdatePermissionRequest
import com.d108.moyeo.domain.repository.BoxMemberRepository
import javax.inject.Inject

class BoxMemberRepositoryImpl @Inject constructor(
    private val boxMemberService: BoxMemberService
): BoxMemberRepository {
    override suspend fun getBoxMembers(boxId: Long): Result<List<BoxMember>> {
        return runCatching {
            val response = boxMemberService.getBoxMembers(boxId)
            if (response.isSuccessful) {
                response.body()?.map { it.toDomain() } ?: emptyList()
            } else {
                throw Exception("Server responded with error: ${response.code()}")
            }
        }
    }

    override suspend fun updateBoxMemberPermission(
        boxId: Long,
        requestList: List<UpdatePermissionRequest>
    ): Result<Unit> {
        return runCatching {
            val dtoList = requestList.map { it.toDto() }

            val response = boxMemberService.updateBoxMemberPermission(
                boxId,dtoList
            )

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                throw Exception("Server responded with error: ${response.code()}")
            }
        }
    }
}