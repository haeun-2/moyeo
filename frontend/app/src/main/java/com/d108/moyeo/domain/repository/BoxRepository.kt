package com.d108.moyeo.domain.repository

import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.model.box.BoxDetail

interface BoxRepository {
    suspend fun createBox(name: String): Result<Long>

    suspend fun getPersonalBox(): Result<Box>
    
    suspend fun getGroupBoxes(size: Int): Result<List<Box>>

    suspend fun getBoxDetail(boxId: Long): Result<BoxDetail>

    suspend fun getBookmarkedBoxes(): Result<List<Box>>

    suspend fun addBookmark(boxId: Long): Result<Unit>

    suspend fun deleteBookmark(boxId: Long): Result<Unit>

}
