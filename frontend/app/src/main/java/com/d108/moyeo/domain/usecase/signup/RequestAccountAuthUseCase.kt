package com.d108.moyeo.domain.usecase.signup

import com.d108.moyeo.data.remote.dto.signup.SessionIdResponseDto
import com.d108.moyeo.domain.repository.SignUpRepository
import javax.inject.Inject

/**
 * 계좌 인증(1원 송금)을 요청하는 비즈니스 로직을 담당하는 UseCase 입니다.
 */
class RequestAccountAuthUseCase @Inject constructor(
    private val signUpRepository: SignUpRepository
) {
    /**
     * @param sessionId 이전 단계에서 발급받은 세션 ID
     * @param email 사용자의 이메일 주소
     * @param bankAccount 사용자가 입력한 계좌번호
     * @return 성공 시 갱신된 sessionId가 담긴 DTO, 실패 시 에러를 포함하는 Result 객체
     */
    suspend operator fun invoke(sessionId: String, email: String, bankAccount: String): Result<SessionIdResponseDto> {
        return signUpRepository.requestAccountAuth(sessionId, email, bankAccount)
    }
}