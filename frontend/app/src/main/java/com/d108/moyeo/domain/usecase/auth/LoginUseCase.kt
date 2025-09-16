package com.d108.moyeo.domain.usecase.auth

import com.d108.moyeo.domain.model.Token
import com.d108.moyeo.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * 실제 로그인을 요청하는 비즈니스 로직을 담당하는 UseCase 입니다.
 * @param authRepository Hilt를 통해 주입받는
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String, fid: String): Result<Token> {
        return authRepository.login(phoneNumber, fid)
    }
}