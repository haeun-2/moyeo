package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.signup.AccountAuthRequestDto
import com.d108.moyeo.data.remote.dto.signup.EmailAuthRequestDto
import com.d108.moyeo.data.remote.dto.signup.PhoneAuthRequestDto
import com.d108.moyeo.data.remote.dto.signup.SessionIdResponseDto
import com.d108.moyeo.data.remote.dto.signup.VerifyAccountCodeRequestDto
import com.d108.moyeo.data.remote.dto.signup.VerifyEmailCodeRequestDto
import com.d108.moyeo.data.remote.dto.signup.VerifyPhoneCodeRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 회원가입(SignUp) 관련 API 명세를 정의하는 Retrofit 서비스 인터페이스입니다.
 */
interface SignUpService {

    /**
     * 서버에 이메일 인증 코드 전송을 요청합니다.
     * @param request 사용자의 이메일 주소가 담긴 DTO
     * @return 성공 시, 다음 단계를 위한 sessionId가 담긴 DTO
     */
    @POST("api/auth/signup/email")  // 서버 명세를 따라감
    suspend fun requestEmailAuth(@Body request: EmailAuthRequestDto): SessionIdResponseDto

    /**
     * 서버에 이메일 인증 코드의 유효성을 검증합니다.
     * @param request sessionId, email, 사용자가 입력한 코드가 담긴 DTO
     * @return 성공 시 별도의 데이터가 없는 성공 응답(200 OK)
     */
    @POST("api/auth/signup/email/verify-code")  // 서버 명세를 따라감
    suspend fun verifyEmailCode(@Body request: VerifyEmailCodeRequestDto): Response<Unit>

    /**
     * 서버에 전화번호 인증 코드 전송을 요청합니다.
     */
    @POST("api/auth/signup/phone")
    suspend fun requestPhoneAuth(@Body request: PhoneAuthRequestDto): SessionIdResponseDto

    /**
     * 서버에 전화번호 인증 코드의 유효성을 검증합니다.
     */
    @POST("api/auth/signup/phone/verify-code")
    suspend fun verifyPhoneCode(@Body request: VerifyPhoneCodeRequestDto): SessionIdResponseDto

    /**
     * 서버에 계좌 인증(1원 송금)을 요청합니다.
     */
    @POST("/api/auth/signup/account")
    suspend fun requestAccountAuth(@Body request: AccountAuthRequestDto): SessionIdResponseDto

    /**
     * 서버에 계좌 1원 인증 코드의 유효성을 검증합니다.
     */
    @POST("/api/auth/signup/account/verify-code")
    suspend fun verifyAccountCode(@Body request: VerifyAccountCodeRequestDto): SessionIdResponseDto


    // TODO: 나중에 전화번호, 계좌, 최종 회원가입 등 다른 API들을 여기에 추가해야 합니다.
}

/*
여기서는 Dto를 바로 쓰는 이유가
 */