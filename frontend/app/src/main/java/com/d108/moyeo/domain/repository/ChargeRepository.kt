package com.d108.moyeo.domain.repository

interface ChargeRepository {
    suspend fun charge(balance: Long): Result<Unit>
}