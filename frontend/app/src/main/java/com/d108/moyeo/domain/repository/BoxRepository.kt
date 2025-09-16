package com.d108.moyeo.domain.repository

import com.d108.moyeo.domain.model.box.Box

interface BoxRepository {
    suspend fun getPersonalBox(): Box
    suspend fun getGroupBoxes(size: Int): List<Box>
}
