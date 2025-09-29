package com.d108.moyeo.domain.usecase.auth

import com.d108.moyeo.domain.model.Token
import com.d108.moyeo.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * [디버그용] 3번 유저로 로그인하는 비즈니스 로직을 담당하는 UseCase (작업 전문가) 입니다.
 * 이 클래스는 오직 '디버그 로그인'이라는 단 하나의 책임만 가집니다.
 * @param authRepository Hilt를 통해 주입받는 '데이터 전문가'
 */
class DebugLoginUseCase4 @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Token> {
        return authRepository.debugLoginUser4()
    }
}