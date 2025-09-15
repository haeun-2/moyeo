package com.d108.moyeo.data.remote.dto.signup

import com.google.gson.annotations.SerializedName

/**
 * 인증 요청/검증 성공 시 서버로부터 받는 공통 응답 데이터.
 * @property sessionId 다음 단계를 위해 서버가 새로 발급하거나 갱신해 준 세션 ID.
 */
data class SessionIdResponseDto(
    @SerializedName("sessionId")
    val sessionId: String
)

// 현재 사용처
/*
회원가입 시
1. 이메일 인증
2. 휴대폰 인증
 */