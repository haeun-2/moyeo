package com.d108.moyeo.data.repository

import com.d108.moyeo.data.remote.api.BoxSettlementService
import com.d108.moyeo.data.remote.dto.banking.SettlementRequestDto
import com.d108.moyeo.domain.repository.BoxSettlementRepository
import javax.inject.Inject

class BoxSettlementRepositoryImpl @Inject constructor(
    private val boxSettlementService: BoxSettlementService
): BoxSettlementRepository {
    override suspend fun settleBox(
        boxId: Long,
        settlements: List<SettlementRequestDto>
    ): Result<Unit> {
        return runCatching {
            val response = boxSettlementService.settleBox(boxId, settlements)
            if (response.isSuccessful) {
                Unit
            } else {
                throw Exception("Server responded with error: ${response.code()}")
            }
        }
    }

}