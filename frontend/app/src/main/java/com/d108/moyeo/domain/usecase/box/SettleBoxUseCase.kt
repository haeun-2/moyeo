package com.d108.moyeo.domain.usecase.box

import com.d108.moyeo.data.remote.dto.banking.SettlementRequestDto
import com.d108.moyeo.domain.repository.BoxRepository
import com.d108.moyeo.domain.repository.BoxSettlementRepository
import javax.inject.Inject

class SettleBoxUseCase @Inject constructor(
    private val boxSettlementRepository: BoxSettlementRepository
) {
    suspend operator fun invoke(boxId: Long, settlements: List<SettlementRequestDto>): Result<Unit> {
        return boxSettlementRepository.settleBox(boxId, settlements)
    }
}