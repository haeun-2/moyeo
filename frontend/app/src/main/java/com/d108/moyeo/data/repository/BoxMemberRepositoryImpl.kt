package com.d108.moyeo.data.repository

import com.d108.moyeo.data.mapper.toDomain
import com.d108.moyeo.data.remote.api.BoxMemberService
import com.d108.moyeo.domain.model.box.BoxMember
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
}