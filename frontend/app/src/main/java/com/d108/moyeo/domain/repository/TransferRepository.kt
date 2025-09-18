package com.d108.moyeo.domain.repository

interface TransferRepository {
    suspend fun transfer(
        fromBoxId: Long,
        toBoxId: Long,
        currency: String,
        amount: Long
    ): Result<Unit>
}