package com.d108.moyeo.domain.repository

import com.d108.moyeo.domain.model.box.Box

interface BoxRepository {
    suspend fun getPersonalBox(): Result<Box>
    suspend fun getGroupBoxes(size: Int): Result<List<Box>>

    suspend fun getBookmarkedBoxes(): Result<List<Box>>
}
