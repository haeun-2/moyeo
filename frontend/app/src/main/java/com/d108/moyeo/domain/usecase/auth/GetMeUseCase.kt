package com.d108.moyeo.domain.usecase.auth

import com.d108.moyeo.data.remote.dto.auth.MeDto
import com.d108.moyeo.domain.repository.AuthRepository
import javax.inject.Inject

class GetMeUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<MeDto> {
        return authRepository.getMe()
    }
}