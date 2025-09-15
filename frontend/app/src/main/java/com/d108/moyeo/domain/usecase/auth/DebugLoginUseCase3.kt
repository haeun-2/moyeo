package com.d108.moyeo.domain.usecase.auth

import com.d108.moyeo.domain.model.Token
import com.d108.moyeo.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * [디버그용] 3번 유저로 로그인하는 비즈니스 로직을 담당하는 UseCase (작업 전문가) 입니다.
 * 이 클래스는 오직 '디버그 로그인'이라는 단 하나의 책임만 가집니다.
 * @param authRepository Hilt를 통해 주입받는 '데이터 전문가'
 */
class DebugLoginUseCase3 @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * 이 클래스의 인스턴스를 함수처럼 호출할 수 있게 해주는 'operator fun invoke' 입니다.
     * ViewModel에서는 이 클래스를 주입받아 'debugLoginUseCase()' 와 같이 바로 호출할 수 있습니다.
     */
    suspend operator fun invoke(): Result<Token> {
        // 자신의 유일한 임무인 '디버그 로그인'을 데이터 전문가에게 요청하고, 결과를 그대로 반환합니다.
        return authRepository.debugLoginUser3()
    }
}