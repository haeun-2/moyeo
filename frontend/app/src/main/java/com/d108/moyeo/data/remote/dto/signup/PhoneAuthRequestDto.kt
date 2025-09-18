package com.d108.moyeo.data.remote.dto.signup

import com.google.gson.annotations.SerializedName

/**
 * 전화번호 인증 코드 전송을 요청할 때 서버로 보낼 데이터.
 * @property sessionId 이전 단계(이메일 인증)에서 발급받은 세션 ID.
 * @property phoneNumber 사용자가 입력한 전화번호.
 */
data class PhoneAuthRequestDto(
    @SerializedName("sessionId")
    val sessionId: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String
)