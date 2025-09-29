package com.d108.moyeo.data.remote.interceptor

import android.util.Log
import com.d108.moyeo.data.local.UserDataManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * 모든 API 요청을 가로채(Intercept), 헤더에 자동으로 인증 토큰을 추가해주는 클래스.
 * Hilt를 통해 @Inject constructor()로 생성되며, 토큰을 가져오기 위해 UserDataManager에 의존합니다.
 */
class AuthInterceptor @Inject constructor(
    private val userDataManager: UserDataManager
) : Interceptor {

    // 모든 Retrofit 요청은 이 intercept 함수를 반드시 통과하게 됩니다.
    override fun intercept(chain: Interceptor.Chain): Response {
        // 1. 원래의 요청(Request)을 가져옵니다.
        val originalRequest = chain.request()

        Log.d("AuthInterceptor", "=== 인터셉터 동작 시작 ===")
        Log.d("AuthInterceptor", "요청 URL: ${originalRequest.url}")

        // 2. "지금 당장 현재 토큰 보고서 한 장 줘" 라고 요청합니다.
        // (네트워크 요청은 이미 백그라운드 스레드에서 일어나므로, 여기서는 runBlocking을 사용해도 안전합니다.)
        val accessToken = runBlocking {
            userDataManager.getAccessToken()
        }

        Log.d("AuthInterceptor", "토큰 존재: ${!accessToken.isNullOrBlank()}")

        // 3. 토큰이 존재한다면, 헤더를 추가한 새로운 요청을 만듭니다.
        val headerRequest = if (!accessToken.isNullOrBlank()) {
            // 토큰 앞뒤 공백 제거 및 검증
            val cleanToken = accessToken.trim()
            Log.d("AuthInterceptor", "토큰 길이: ${cleanToken.length}")
            Log.d("AuthInterceptor", "토큰 시작: ${cleanToken.take(20)}...")

            // JWT 토큰 형식 검증 (간단한 체크)
            if (cleanToken.contains(".") && cleanToken.split(".").size == 3) {
                Log.d("AuthInterceptor", "JWT 형식 검증 통과")
                // 서버와 약속된 형식("Bearer <토큰>")으로 "Authorization" 헤더를 추가합니다.
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer $cleanToken")
                    .header("Content-Type", "application/json")
                    .build()
            } else {
                Log.e("AuthInterceptor", "잘못된 JWT 토큰 형식")
                originalRequest
            }
        } else {
            // 토큰이 없다면 (로그인 전), 원래의 요청을 그대로 사용합니다.
            Log.e("AuthInterceptor", "토큰이 없음")
            originalRequest
        }

        // 4. 헤더가 추가된 최종 요청을 서버로 보내고, 그 응답을 반환합니다.
        val response = chain.proceed(headerRequest)

        Log.d("AuthInterceptor", "응답 코드: ${response.code}")

        // 401 응답 시 상세 로깅 (토큰 만료 또는 인증 실패)
        if (response.code == 401) {
            Log.e("AuthInterceptor", "401 Unauthorized 응답")
            Log.e("AuthInterceptor", "응답 헤더: ${response.headers}")

            // 응답 본문 로깅 (디버깅용)
            val responseBody = response.peekBody(1024).string()
            Log.e("AuthInterceptor", "401 응답 본문: $responseBody")
        }

        return response
    }
}