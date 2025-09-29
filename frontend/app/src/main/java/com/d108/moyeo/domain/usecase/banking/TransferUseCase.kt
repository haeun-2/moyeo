package com.d108.moyeo.domain.usecase.banking

import com.d108.moyeo.domain.repository.TransferRepository
import javax.inject.Inject

class TransferUseCase @Inject constructor(
    private val repo: TransferRepository
) {
    suspend operator fun invoke(
        fromBoxId: Long,
        toBoxId: Long,
        currency: String,
        amount: Long
    ) = repo.transfer(fromBoxId, toBoxId, currency, amount)
}
