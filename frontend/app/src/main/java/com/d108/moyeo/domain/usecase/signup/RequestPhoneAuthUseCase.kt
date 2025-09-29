package com.d108.moyeo.domain.usecase.signup

import com.d108.moyeo.data.remote.dto.signup.SessionIdResponseDto
import com.d108.moyeo.domain.repository.SignUpRepository
import javax.inject.Inject

/**
 * 전화번호 인증 코드 전송을 요청하는 비즈니스 로직을 담당하는 UseCase 입니다.
 */
class RequestPhoneAuthUseCase @Inject constructor(
    private val signUpRepository: SignUpRepository
) {
    suspend operator fun invoke(sessionId: String, phoneNumber: String): Result<SessionIdResponseDto> {
        return signUpRepository.requestPhoneAuth(sessionId, phoneNumber)
    }
}
