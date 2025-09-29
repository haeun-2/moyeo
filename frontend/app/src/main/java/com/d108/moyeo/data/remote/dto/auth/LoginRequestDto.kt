package com.d108.moyeo.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

/**
 * 실제 로그인 요청 시 서버로 전송할 데이터의 구조를 정의하는 클래스 (Data Transfer Object).
 * @property phoneNumber 사용자의 전화번호.
 * @property fid 앱 설치 아이디
 */
data class LoginRequestDto(
    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("fid")
    val fid: String
)