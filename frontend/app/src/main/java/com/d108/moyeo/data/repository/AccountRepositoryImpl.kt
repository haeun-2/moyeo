package com.d108.moyeo.data.repository

import android.util.Log
import com.d108.moyeo.data.mapper.toDomain
import com.d108.moyeo.data.remote.api.AccountService
import com.d108.moyeo.data.remote.dto.account.AccountConnectRequestDto
import com.d108.moyeo.data.remote.dto.account.AccountVerificationRequestDto
import com.d108.moyeo.domain.model.ConnectedAccount
import com.d108.moyeo.domain.repository.AccountRepository
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val service: AccountService
) : AccountRepository {

    override suspend fun getConnectedAccount(): Result<ConnectedAccount> = runCatching {
        service.getConnectedAccount().toDomain()
    }

    override suspend fun requestAccountVerification(
        bankCode: String,
        bankAccount: String
    ): Result<Unit> = runCatching {
        val res = service.requestAccountVerification(
            AccountVerificationRequestDto(bankCode, bankAccount)
        )
        if (!res.isSuccessful) throw IllegalStateException("Verification request failed: ${res.code()}")
    }

    override suspend fun connectAccount(
        bankCode: String,
        bankAccount: String,
        verificationCode: String
    ): Result<Unit> = runCatching {
        val res = service.connectAccount(
            AccountConnectRequestDto(bankCode, bankAccount, verificationCode)
        )
        if (!res.isSuccessful) {
            val msg = res.errorBody()?.string()?.take(500)
            Log.e("AccountRepositoryImpl", "connectAccount error ${res.code()} body=$msg")
            throw IllegalStateException("Connect account failed: ${res.code()} ${msg ?: ""}".trim())
        }
    }
}