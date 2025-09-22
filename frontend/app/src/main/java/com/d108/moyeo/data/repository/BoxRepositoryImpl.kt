package com.d108.moyeo.data.repository

import com.d108.moyeo.data.mapper.toDomain
import com.d108.moyeo.data.remote.api.BoxService
import com.d108.moyeo.data.remote.dto.box.CreateBoxRequestDto
import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.repository.BoxRepository
import javax.inject.Inject
import com.d108.moyeo.domain.model.box.BoxDetail
import com.d108.moyeo.domain.model.box.InviteLinkResult

class BoxRepositoryImpl @Inject constructor(
    private val api: BoxService
) : BoxRepository {

    override suspend fun createBox(name: String): Result<Long> {
        return runCatching {
            val response = api.createBox(CreateBoxRequestDto(name))
            if (response.isSuccessful) {
                response.body()?.boxId ?: throw Exception("Response body is null")
            } else {
                throw Exception("Server responded with error: ${response.code()}")
            }
        }
    }

    override suspend fun createInviteLink(boxId: Long): Result<InviteLinkResult> {
        return runCatching {
            val response = api.createInviteLink(boxId)
            if (response.isSuccessful) {
                val body = response.body() ?: throw NullPointerException("Response body is null")
                InviteLinkResult(
                    inviteLink = body.inviteLink,
                    inviteCode = body.inviteCode,
                    expiresAt  = body.expiresAt
                )
            } else {
                throw Exception("Server responded with error code: ${response.code()}")
            }
        }
    }

    override suspend fun joinBox(code: String): Result<Long> {
        return runCatching {
            val response = api.joinBox(code)
            if (response.isSuccessful) {
                response.body()?.boxId ?: throw NullPointerException("Response body is null")
            } else {
                throw Exception("Server responded with error code: ${response.code()}")
            }
        }
    }

    override suspend fun getPersonalBox(): Result<Box> {
        return runCatching {
            val response = api.getPersonalBox()
            if (response.isSuccessful) {
                response.body()?.toDomain() ?: throw Exception("Response body is null")
            } else {
                throw Exception("Server responded with error: ${response.code()}")
            }
        }
    }

    override suspend fun getGroupBoxes(size: Int): Result<List<Box>> {
        return runCatching {
            val response = api.getGroupBoxes(size)
            if (response.isSuccessful) {
                response.body()?.map { it.toDomain() } ?: emptyList()
            } else {
                throw Exception("Server responded with error: ${response.code()}")
            }
        }
    }

    override suspend fun getBoxDetail(boxId: Long): Result<BoxDetail> {
        return runCatching {
            val response = api.getBoxDetail(boxId)
            if (response.isSuccessful) {
                response.body()?.toDomain() ?: throw NullPointerException("Response body is null")
            } else {
                throw Exception("Server responded with error code: ${response.code()}")
            }
        }
    }

    override suspend fun getBookmarkedBoxes(): Result<List<Box>> {
        return runCatching {
            val response = api.getBookmarkedBoxes()
            if (response.isSuccessful) {
                // 응답받은 DTO 리스트를 Domain 모델 리스트로 변환
                response.body()?.map { it.toDomain() } ?: emptyList()
            } else {
                throw Exception("Server responded with error code: ${response.code()}")
            }
        }
    }

    override suspend fun addBookmark(boxId: Long): Result<Unit> {
        return runCatching {
            val response = api.addBookmark(boxId)
            if (!response.isSuccessful) {
                throw Exception("Server responded with error code: ${response.code()}")
            }
        }
    }


    override suspend fun deleteBookmark(boxId: Long): Result<Unit> {
        return runCatching {
            val response = api.deleteBookmark(boxId)
            if (!response.isSuccessful) {
                throw Exception("Server responded with error code: ${response.code()}")
            }
        }
    }
}

