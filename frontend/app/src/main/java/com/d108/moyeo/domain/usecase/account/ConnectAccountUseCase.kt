package com.d108.moyeo.domain.usecase.account

import com.d108.moyeo.domain.repository.AccountRepository
import javax.inject.Inject

class ConnectAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(bankCode: String, bankAccount: String, verificationCode: String) =
        repository.connectAccount(bankCode, bankAccount, verificationCode)
}