package com.d108.moyeo.domain.usecase.account

import com.d108.moyeo.domain.repository.AccountRepository
import javax.inject.Inject

class RequestAccountVerificationUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(bankCode: String, bankAccount: String) =
        repository.requestAccountVerification(bankCode, bankAccount)
}