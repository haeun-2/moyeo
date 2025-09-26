package com.d108.moyeo.domain.repository

import com.d108.moyeo.domain.model.box.BoxMember
import com.d108.moyeo.domain.model.box.UpdatePermissionRequest

interface BoxMemberRepository {

    suspend fun getBoxMembers(boxId: Long): Result<List<BoxMember>>

    suspend fun updateBoxMemberPermission(boxId: Long, requestList: List<UpdatePermissionRequest>)
        :Result<Unit>
}