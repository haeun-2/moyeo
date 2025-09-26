package com.d108.moyeo.data.repository

import android.util.Log
import com.d108.moyeo.data.local.UserDataManager // ▼▼▼ AuthDataStore 대신 UserDataManager를 import 합니다. ▼▼▼
import com.d108.moyeo.data.mapper.toDomain
import com.d108.moyeo.data.remote.api.AuthService
import com.d108.moyeo.data.remote.dto.auth.LoginRequestDto
import com.d108.moyeo.domain.model.Token
import com.d108.moyeo.domain.repository.AuthRepository
import com.d108.moyeo.domain.usecase.fcm.RegisterFcmTokenUseCase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


private val TAG = "AuthRepositoryImpl"
/**
 * AuthRepository 인터페이스의 구현체 (실제 일꾼) 입니다.
 * @param authService Hilt를 통해 주입받은, 실제 네트워크 통신을 담당하는 Retrofit 서비스
 * @param userDataManager Hilt를 통해 주입받은, 사용자 데이터를 기기에 저장
 */
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val userDataManager: UserDataManager, // 받는 타입을 UserDataManager로 변경
    private val registerFcmTokenUseCase: RegisterFcmTokenUseCase
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
                userDataManager.saveBiometricsPreference(true)  // TODO: 이거 트루 맞아? 데이터스토어에서 받아와야 하는 거 아님???

                try {
                    val fcmToken = FirebaseMessaging.getInstance().token.await()
                    Log.d(TAG, "3번 FCM 토큰 가져오기 성공: $fcmToken")
                    registerFcmTokenUseCase(fcmToken)
                        .onSuccess { Log.d(TAG, "3번 FCM 토큰 서버 등록 완료") }
                        .onFailure { Log.e(TAG, "3번 FCM 토큰 서버 등록 실패", it) }
                } catch (e: Exception) {
                    Log.e(TAG, "FCM 토큰 가져오기 실패", e)
                    // FCM 토큰 가져오기/등록 실패가 전체 로그인 실패를 의미하지는 않으므로,
                    // 에러만 로깅하고 계속 진행합니다.
                }
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

                try {
                    val fcmToken = FirebaseMessaging.getInstance().token.await()
                    Log.d(TAG, "4번 FCM 토큰 가져오기 성공: $fcmToken")
                    registerFcmTokenUseCase(fcmToken)
                        .onSuccess { Log.d(TAG, "4번 FCM 토큰 서버 등록 완료") }
                        .onFailure { Log.e(TAG, "4번 FCM 토큰 서버 등록 실패", it) }
                } catch (e: Exception) {
                    Log.e(TAG, "FCM 토큰 가져오기 실패", e)
                    // FCM 토큰 가져오기/등록 실패가 전체 로그인 실패를 의미하지는 않으므로,
                    // 에러만 로깅하고 계속 진행합니다.
                }
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