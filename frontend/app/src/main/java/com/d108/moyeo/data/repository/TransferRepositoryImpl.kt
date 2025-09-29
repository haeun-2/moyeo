package com.d108.moyeo.data.repository

import com.d108.moyeo.data.remote.api.BankingService
import com.d108.moyeo.data.remote.dto.banking.TransferRequestDto
import com.d108.moyeo.domain.repository.TransferRepository
import javax.inject.Inject

class TransferRepositoryImpl @Inject constructor(
    private val api: BankingService
) : TransferRepository {

    override suspend fun transfer(
        fromBoxId: Long,
        toBoxId: Long,
        currency: String,
        amount: Long
    ): Result<Unit> = runCatching {
        val response = api.transfer(
            TransferRequestDto(
                fromBoxId = fromBoxId,
                toBoxId = toBoxId,
                currency = currency,
                amount = amount
            )
        )
        if (!response.isSuccessful) {
            throw IllegalStateException("Transfer failed: HTTP ${response.code()}")
        } else {
            Unit
        }
    }
}
