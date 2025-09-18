package com.d108.moyeo.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

/**
 * 로그인 성공 시 서버로부터 받는 공통 응답 데이터의 구조를 정의하는 클래스 (Data Transfer Object).
 * @property accessToken API 요청 시 사용될 단기 인증 토큰.
 * @property refreshToken accessToken 만료 시 재발급을 위해 사용될 장기 토큰.
 */
data class LoginResponseDto(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String
)