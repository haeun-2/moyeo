package com.d108.moyeo.data.remote.interceptor

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

        // 2. "지금 당장 현재 토큰 보고서 한 장 줘" 라고 요청합니다.
        //    (네트워크 요청은 이미 백그라운드 스레드에서 일어나므로, 여기서는 runBlocking을 사용해도 안전합니다.)
        val accessToken = runBlocking {
            userDataManager.getAccessToken()
        }

        // 3. 토큰이 존재한다면, 헤더를 추가한 새로운 요청을 만듭니다.
        val headerRequest = if (!accessToken.isNullOrBlank()) {
            // 서버와 약속된 형식("Bearer <토큰>")으로 "Authorization" 헤더를 추가합니다.
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            // 토큰이 없다면 (로그인 전), 원래의 요청을 그대로 사용합니다.
            originalRequest
        }

        // 4. 헤더가 추가된 최종 요청을 서버로 보내고, 그 응답을 반환합니다.
        return chain.proceed(headerRequest)
    }
}