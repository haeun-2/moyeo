package com.d108.moyeo.data.remote.dto.signup

import com.google.gson.annotations.SerializedName

/**
 * 계좌 인증(1원 송금)을 요청할 때 서버로 보낼 데이터의 구조를 정의합니다.
 * @property sessionId 이전 단계에서 발급받은 세션 ID.
 * @property email 사용자의 이메일 주소.
 * @property bankAccount 사용자가 입력한 계좌번호.
 */
data class AccountAuthRequestDto(
    @SerializedName("sessionId")
    val sessionId: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("bankAccount")
    val bankAccount: String
)