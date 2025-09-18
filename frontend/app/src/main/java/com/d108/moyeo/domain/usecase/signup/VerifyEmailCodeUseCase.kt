package com.d108.moyeo.domain.usecase.signup

import com.d108.moyeo.domain.repository.SignUpRepository
import javax.inject.Inject

/**
 * 사용자가 입력한 이메일 인증 코드를 검증하는 비즈니스 로직을 담당하는 UseCase 입니다.
 */
class VerifyEmailCodeUseCase @Inject constructor(
    private val signUpRepository: SignUpRepository
) {
    suspend operator fun invoke(sessionId: String, email: String, emailCode: String): Result<Unit> {
        return signUpRepository.verifyEmailCode(sessionId, email, emailCode)
    }
}