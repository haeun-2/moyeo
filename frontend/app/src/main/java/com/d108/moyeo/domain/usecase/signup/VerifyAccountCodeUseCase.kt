package com.d108.moyeo.domain.usecase.signup

import com.d108.moyeo.data.remote.dto.signup.SessionIdResponseDto
import com.d108.moyeo.domain.repository.SignUpRepository
import javax.inject.Inject

/**
 * 사용자가 입력한 1원 인증 코드를 검증하는 비즈니스 로직을 담당하는 UseCase 입니다.
 */
class VerifyAccountCodeUseCase @Inject constructor(
    private val signUpRepository: SignUpRepository
) {
    suspend operator fun invoke(sessionId: String, email: String, bankAccount: String, code: String): Result<SessionIdResponseDto> {
        return signUpRepository.verifyAccountCode(sessionId, email, bankAccount, code)
    }
}