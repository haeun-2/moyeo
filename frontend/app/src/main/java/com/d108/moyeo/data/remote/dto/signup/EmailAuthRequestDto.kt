package com.d108.moyeo.data.remote.dto.signup

import com.google.gson.annotations.SerializedName

/**
 * 이메일 인증 코드 전송을 요청할 때 서버로 보낼 데이터의 구조를 정의하는 클래스 (Data Transfer Object).
 * @property email 사용자가 입력한 이메일 주소.
 */
data class EmailAuthRequestDto(
    // @SerializedName 어노테이션은, 실제 JSON 데이터의 키(key) 이름이 "email"임을 명시합니다.
    @SerializedName("email")  // 서버에서 보내는 "email"이라는 키를
    val email: String  // 안드로이드 앱에서는 "email"이라는 변수에 할당
)