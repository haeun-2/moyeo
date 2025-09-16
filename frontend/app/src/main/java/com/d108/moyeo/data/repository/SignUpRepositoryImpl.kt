package com.d108.moyeo.data.repository

import com.d108.moyeo.data.remote.api.SignUpService
import com.d108.moyeo.data.remote.dto.signup.AccountAuthRequestDto
import com.d108.moyeo.data.remote.dto.signup.EmailAuthRequestDto
import com.d108.moyeo.data.remote.dto.signup.PhoneAuthRequestDto
import com.d108.moyeo.data.remote.dto.signup.SessionIdResponseDto
import com.d108.moyeo.data.remote.dto.signup.SignUpRequestDto
import com.d108.moyeo.data.remote.dto.signup.VerifyAccountCodeRequestDto
import com.d108.moyeo.data.remote.dto.signup.VerifyEmailCodeRequestDto
import com.d108.moyeo.data.remote.dto.signup.VerifyPhoneCodeRequestDto
import com.d108.moyeo.domain.model.SignUpInfo
import com.d108.moyeo.domain.repository.SignUpRepository
import javax.inject.Inject

/**
 * SignUpRepository 인터페이스의 구현체 (실제 일꾼) 입니다.
 * @param signUpService Hilt를 통해 주입받은, 실제 네트워크 통신을 담당하는 Retrofit 서비스
 */
class SignUpRepositoryImpl @Inject constructor(
    private val signUpService: SignUpService
) : SignUpRepository {

    /**
     * 이메일 인증을 요청하고, 그 결과를 Result로 감싸서 반환합니다.
     */
    override suspend fun requestEmailAuth(email: String): Result<SessionIdResponseDto> {
        // runCatching은 람다 블록 안의 코드를 실행하다가 예외(Exception)가 발생하면,
        // 앱을 중단시키는 대신 Result.failure(에러)를 반환해주는 안전장치입니다.
        return runCatching {
            // Retrofit 서비스(signUpService)를 통해 실제 API를 호출합니다.
            signUpService.requestEmailAuth(EmailAuthRequestDto(email = email))
        }
    }

    /**
     * 이메일 인증 코드를 검증하고, 그 결과를 Result로 감싸서 반환합니다.
     */
    override suspend fun verifyEmailCode(sessionId: String, email: String, emailCode: String): Result<Unit> {
        return runCatching {
            // 서버 응답이 Response<Unit>이므로, isSuccessful을 확인하여 성공/실패를 직접 분기 처리합니다.
            val response = signUpService.verifyEmailCode(
                VerifyEmailCodeRequestDto(sessionId = sessionId, email = email, emailCode = emailCode)
            )
            if (!response.isSuccessful) {
                // 서버가 2xx 범위가 아닌 응답 코드(4xx, 5xx 등)를 반환하면 에러를 발생시킵니다.
                throw Exception("Server responded with error: ${response.code()}")
            }
            // 성공 시에는 별다른 데이터 없이 Unit을 반환합니다.
        }
    }

    override suspend fun requestPhoneAuth(sessionId: String, phoneNumber: String): Result<SessionIdResponseDto> = runCatching {
        signUpService.requestPhoneAuth(
            PhoneAuthRequestDto(
                sessionId = sessionId,
                phoneNumber = phoneNumber
            )
        )
    }

    override suspend fun verifyPhoneCode(sessionId: String, phoneNumber: String, phoneCode: String): Result<SessionIdResponseDto> = runCatching {
        signUpService.verifyPhoneCode(
            VerifyPhoneCodeRequestDto(
                sessionId = sessionId,
                phoneNumber = phoneNumber,
                phoneCode = phoneCode
            )
        )
    }

    override suspend fun requestAccountAuth(sessionId: String, email: String, bankAccount: String): Result<SessionIdResponseDto> = runCatching {
        signUpService.requestAccountAuth(
            AccountAuthRequestDto(
                sessionId = sessionId,
                email = email,
                bankAccount = bankAccount
            )
        )
    }

    override suspend fun verifyAccountCode(sessionId: String, email: String, bankAccount: String, code: String): Result<SessionIdResponseDto> = runCatching {
        signUpService.verifyAccountCode(
            VerifyAccountCodeRequestDto(
                sessionId = sessionId,
                email = email,
                bankAccount = bankAccount,
                accountCode = code // DTO 필드명에 맞게 accountCode 사용
            )
        )
    }

    override suspend fun signUp(sessionId: String, signUpInfo: SignUpInfo): Result<Unit> = runCatching {
        val requestDto = SignUpRequestDto(
            sessionId = sessionId,
            name = signUpInfo.name,
            email = signUpInfo.email,
            phoneNumber = signUpInfo.phoneNumber,
            fid = signUpInfo.fid,
            connectedBankCode = signUpInfo.bankCode,
            connectedBankAccount = signUpInfo.accountNumber
        )

        val response = signUpService.signUp(requestDto)

        if (!response.isSuccessful) {
            throw Exception("Server responded with error: ${response.code()}")
        }
    }
}