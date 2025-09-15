package com.d108.moyeo.data.remote.dto.signup

import com.google.gson.annotations.SerializedName

/**
 * 이메일 인증 코드를 검증할 때 서버로 보낼 데이터.
 * @property sessionId 이전에 이메일 인증 요청 시 발급받은 세션 ID.
 * @property email 인증을 진행 중인 이메일 주소.
 * @property emailCode 사용자가 입력한 인증 코드.
 */
data class VerifyEmailCodeRequestDto(
    @SerializedName("sessionId")
    val sessionId: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("verificationCode")
    val emailCode: String
)