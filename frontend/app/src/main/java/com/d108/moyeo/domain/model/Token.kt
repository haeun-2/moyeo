package com.d108.moyeo.domain.model

/**
 * UI 계층 등 앱의 핵심 로직에서 사용할, 깔끔하게 정제된 토큰 데이터 모델입니다.
 */
data class Token(
    val accessToken: String,
    val refreshToken: String
)