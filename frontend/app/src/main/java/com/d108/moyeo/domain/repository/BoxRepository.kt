package com.d108.moyeo.domain.repository

import com.d108.moyeo.domain.model.box.Box

interface BoxRepository {
    suspend fun createBox(name: String): Result<Long>

    suspend fun getPersonalBox(): Result<Box>
    
    suspend fun getGroupBoxes(size: Int): Result<List<Box>>

    suspend fun getBookmarkedBoxes(): Result<List<Box>>

    suspend fun addBookmark(boxId: Long): Result<Unit>

    suspend fun deleteBookmark(boxId: Long): Result<Unit>

}
