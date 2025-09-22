package com.d108.moyeo.data.repository

import com.d108.moyeo.data.remote.api.BankingService
import com.d108.moyeo.data.remote.dto.banking.ChargeRequestDto
import com.d108.moyeo.domain.repository.ChargeRepository
import javax.inject.Inject

class ChargeRepositoryImpl @Inject constructor(
    private val api: BankingService
) : ChargeRepository {
    override suspend fun charge(balance: Long): Result<Unit> = runCatching {
        val res = api.charge(ChargeRequestDto(balance))
        if (!res.isSuccessful) error("HTTP ${res.code()}")
        val body = res.body() ?: error("Empty body")
        if (!body.isSuccess) error(body.errorMessage ?: "Deposit failed")
    }
}