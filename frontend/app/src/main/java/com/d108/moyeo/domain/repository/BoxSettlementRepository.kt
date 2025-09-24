package com.d108.moyeo.domain.repository

import com.d108.moyeo.data.remote.dto.banking.SettlementRequestDto

interface BoxSettlementRepository {
    suspend fun settleBox(boxId: Long, settlements: List<SettlementRequestDto>): Result<Unit>
}