package com.d108.moyeo.data.repository

import com.d108.moyeo.data.local.UserDataManager // ▼▼▼ AuthDataStore 대신 UserDataManager를 import 합니다. ▼▼▼
import com.d108.moyeo.data.mapper.toDomain
import com.d108.moyeo.data.remote.api.AuthService
import com.d108.moyeo.data.remote.dto.auth.LoginRequestDto
import com.d108.moyeo.domain.model.Token
import com.d108.moyeo.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * AuthRepository 인터페이스의 구현체 (실제 일꾼) 입니다.
 * @param authService Hilt를 통해 주입받은, 실제 네트워크 통신을 담당하는 Retrofit 서비스
 * @param userDataManager Hilt를 통해 주입받은, 사용자 데이터를 기기에 저장
 */
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val userDataManager: UserDataManager // 받는 타입을 UserDataManager로 변경
) : AuthRepository {

    // 실시간 FLow를 그대로 외부에 노출
    override val accessToken: Flow<String?> = userDataManager.accessTokenFlow
    override val refreshToken: Flow<String?> = userDataManager.refreshTokenFlow

    override suspend fun debugLoginUser3(): Result<Token> {
        return runCatching {
            val response = authService.debugLoginUser3()
            if (response.isSuccessful && response.body() != null) {
                val tokenDto = response.body()!!
                userDataManager.saveTokens(tokenDto.accessToken, tokenDto.refreshToken)
                userDataManager.savePin("111111")
                userDataManager.saveBiometricsPreference(true)
                tokenDto.toDomain()
            } else {
                throw Exception("Server responded with error: ${response.code()}")
            }
        }
    }

    override suspend fun debugLoginUser4(): Result<Token> {
        return runCatching {
            val response = authService.debugLoginUser4()
            if (response.isSuccessful && response.body() != null) {
                val tokenDto = response.body()!!
                userDataManager.saveTokens(tokenDto.accessToken, tokenDto.refreshToken)
                userDataManager.savePin("111111")
                userDataManager.saveBiometricsPreference(true)
                tokenDto.toDomain()
            } else {
                throw Exception("Server responded with error: ${response.code()}")
            }
        }
    }

    override suspend fun login(phoneNumber: String, fid: String): Result<Token> {
        return runCatching {
            val response = authService.login(LoginRequestDto(phoneNumber = phoneNumber, fid = fid))
            if (response.isSuccessful && response.body() != null) {
                val tokenDto = response.body()!!
                userDataManager.saveTokens(tokenDto.accessToken, tokenDto.refreshToken)
                tokenDto.toDomain()
            } else {
                throw Exception("Server responded with error: ${response.code()}")
            }
        }
    }
}