package com.d108.moyeo.domain.repository

import com.d108.moyeo.domain.model.ConnectedAccount

interface AccountRepository {
    suspend fun getConnectedAccount(): Result<ConnectedAccount>

    suspend fun requestAccountVerification(
        bankCode: String, bankAccount: String
    ): Result<Unit>

    suspend fun connectAccount(
        bankCode: String, bankAccount: String, verificationCode: String
    ): Result<Unit>
}