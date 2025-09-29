package com.d108.moyeo.data.mapper

import com.d108.moyeo.data.remote.dto.auth.LoginResponseDto
import com.d108.moyeo.domain.model.Token

/**
 * LoginResponseDto(서버용 데이터)를 Token(앱용 데이터) 모델로 변환하는 확장 함수입니다.
 */
fun LoginResponseDto.toDomain(): Token {
    return Token(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken
    )
}