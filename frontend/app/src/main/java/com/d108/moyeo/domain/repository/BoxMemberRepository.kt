package com.d108.moyeo.domain.repository

import com.d108.moyeo.domain.model.box.BoxMember

interface BoxMemberRepository {

    suspend fun getBoxMembers(boxId: Long): Result<List<BoxMember>>
}