package com.d108.moyeo.data.repository

import com.d108.moyeo.data.mapper.toDomain
import com.d108.moyeo.data.remote.api.BoxService
import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.repository.BoxRepository
import javax.inject.Inject

class BoxRepositoryImpl @Inject constructor(
    private val api: BoxService
) : BoxRepository {

    override suspend fun getPersonalBox(): Box {
        return api.getMyPersonalBox().toDomain()
    }

    override suspend fun getGroupBoxes(page: Int, size: Int): List<Box> {
        return api.getGroupBoxes(page, size).map { it.toDomain() }
    }
}
