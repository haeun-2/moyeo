package com.d108.moyeo.domain.usecase.banking

import com.d108.moyeo.domain.repository.ChargeRepository
import javax.inject.Inject

class ChargeUseCase @Inject constructor(
    private val repository: ChargeRepository
) {
    suspend operator fun invoke(balance: Long): Result<Unit> = repository.charge(balance)
}