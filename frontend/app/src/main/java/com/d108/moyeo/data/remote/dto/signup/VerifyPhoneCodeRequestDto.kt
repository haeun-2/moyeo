package com.d108.moyeo.data.remote.dto.signup

import com.google.gson.annotations.SerializedName

/**
 * 전화번호 인증 코드를 검증할 때 서버로 보낼 데이터.
 * @property sessionId 현재 회원가입 과정을 식별하는 세션 ID.
 * @property phoneNumber 인증을 진행 중인 전화번호.
 * @property phoneCode 사용자가 입력한 인증 코드.
 */
data class VerifyPhoneCodeRequestDto(
    @SerializedName("sessionId")
    val sessionId: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("verificationCode")
    val phoneCode: String
)