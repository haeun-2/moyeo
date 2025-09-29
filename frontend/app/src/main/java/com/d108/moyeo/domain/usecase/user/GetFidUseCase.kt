package com.d108.moyeo.domain.usecase.user

import com.d108.moyeo.domain.repository.FidRepository
import javax.inject.Inject

class GetFidUseCase @Inject constructor(
    private val fidRepository: FidRepository
) {
    suspend operator fun invoke(): Result<String> {
        return fidRepository.getFid()
    }
}