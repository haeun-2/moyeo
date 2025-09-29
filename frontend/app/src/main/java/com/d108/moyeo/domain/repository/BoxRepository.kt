package com.d108.moyeo.domain.repository

import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.model.box.BoxDetail
import com.d108.moyeo.domain.model.box.InviteLinkResult

interface BoxRepository {
    suspend fun createBox(name: String): Result<Long>

    suspend fun createInviteLink(boxId: Long): Result<InviteLinkResult>

    suspend fun joinBox(code: String): Result<Long>

    suspend fun getPersonalBox(): Result<Box>

    suspend fun getPaymentBoxes(): Result<List<Box>>
    
    suspend fun getGroupBoxes(size: Int): Result<List<Box>>

    suspend fun getBoxDetail(boxId: Long): Result<BoxDetail>

    suspend fun getBookmarkedBoxes(): Result<List<Box>>

    suspend fun addBookmark(boxId: Long): Result<Unit>

    suspend fun deleteBookmark(boxId: Long): Result<Unit>

}
