package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.auth.LoginRequestDto
import com.d108.moyeo.data.remote.dto.auth.LoginResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 인증(Auth) 관련 API 명세를 정의하는 Retrofit 서비스 인터페이스입니다.
 */
interface AuthService {

    /**
     * [디버그용] 유저 3번으로 로그인하여 토큰을 발급받는 API입니다.
     * 요청 본문(Request Body)이 필요 없습니다.
     * @return 로그인 성공 시 토큰 정보가 담긴 DTO
     */
    @POST("/api/v1/users/test")
    suspend fun debugLoginUser3(): Response<LoginResponseDto>

    /**
     * [디버그용] 유저 4번으로 로그인하여 토큰을 발급받는 API입니다.
     * 요청 본문(Request Body)이 필요 없습니다.
     * @return 로그인 성공 시 토큰 정보가 담긴 DTO
     */
    @POST("/api/v1/users/test2")
    suspend fun debugLoginUser4(): Response<LoginResponseDto>

    /**
     * 실제 로그인을 요청하는 API입니다.
     * @param loginRequest 로그인에 필요한 전화번호와 fid가 담긴 DTO
     * @return 로그인 성공 시 토큰 정보가 담긴 DTO
     */
    @POST("/api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequestDto): Response<LoginResponseDto>

    // TODO: 나중에 로그아웃, 토큰 재발급 등 다른 API들을 여기에 추가해야 합니다
}