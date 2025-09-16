package com.d108.moyeo.data.repository

import com.d108.moyeo.data.mapper.toDomain
import com.d108.moyeo.data.remote.api.BoxService
import com.d108.moyeo.data.remote.dto.box.CreateBoxRequestDto
import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.repository.BoxRepository
import javax.inject.Inject

class BoxRepositoryImpl @Inject constructor(
    private val api: BoxService
) : BoxRepository {


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

}

