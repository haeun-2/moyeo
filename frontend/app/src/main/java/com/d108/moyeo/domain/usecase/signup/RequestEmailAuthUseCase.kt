package com.d108.moyeo.domain.usecase.signup

import com.d108.moyeo.domain.repository.SignUpRepository
import javax.inject.Inject

class RequestEmailAuthUseCase @Inject constructor(
    private val signUpRepository: SignUpRepository
) {
    // invoke 함수는 이 클래스 자체를 함수처럼 호출할 수 있게 해줍니다.
    // 예: requestEmailAuthUseCase("test@example.com")
    suspend operator fun invoke(email: String): Result<String> {
        return signUpRepository.requestEmailAuth(email).map { it.sessionId }
    }
}