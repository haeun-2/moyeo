package com.d108.moyeo.domain.usecase.account


import com.d108.moyeo.domain.model.ConnectedAccount
import com.d108.moyeo.domain.repository.AccountRepository
import javax.inject.Inject

class GetConnectedAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(): Result<ConnectedAccount> {
        return repository.getConnectedAccount()
    }
}