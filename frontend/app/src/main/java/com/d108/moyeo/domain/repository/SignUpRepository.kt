package com.d108.moyeo.domain.repository

import com.d108.moyeo.data.remote.dto.signup.SessionIdResponseDto

/**
 * 회원가입 기능과 관련된 데이터 처리를 위한 인터페이스 (설계도).
 * ViewModel은 이 인터페이스에만 의존하게 되어, 실제 구현(Impl)이 어떻게 바뀌든 영향을 받지 않습니다.
 */
interface SignUpRepository {

    /**
     * 서버에 이메일 인증 코드 전송을 요청합니다.
     * @param email 사용자가 입력한 이메일 주소
     * @return 성공 시 sessionId가 담긴 DTO, 실패 시 에러를 포함하는 Result 객체
     */
    // 각 함수는 suspend로 선언하여 비동기적으로(코루틴 내에서) 동작하도록 합니다.
    // Result<T>를 사용하면, 네트워크 통신의 성공(Success)과 실패(Failure)를 명확하고 안전하게 처리할 수 있습니다.
    suspend fun requestEmailAuth(email: String): Result<SessionIdResponseDto>

    /**
     * 서버에 이메일 인증 코드의 유효성을 검증합니다.
     * @param sessionId 이전에 발급받은 세션 ID
     * @param email 인증을 진행 중인 이메일 주소
     * @param emailCode 사용자가 입력한 인증 코드
     * @return 성공 시 Unit, 실패 시 에러를 포함하는 Result 객체
     */
    suspend fun verifyEmailCode(sessionId: String, email: String, emailCode: String): Result<Unit>

    /**
     * 서버에 전화번호 인증 코드 전송을 요청합니다.
     * @param sessionId 이전 단계에서 발급받은 세션 ID
     * @param phoneNumber 사용자가 입력한 전화번호
     * @return 성공 시 갱신된 sessionId가 담긴 DTO, 실패 시 에러를 포함하는 Result 객체
     */
    suspend fun requestPhoneAuth(sessionId: String, phoneNumber: String): Result<SessionIdResponseDto>

    /**
     * 서버에 전화번호 인증 코드의 유효성을 검증합니다.
     * @param sessionId 현재 세션 ID
     * @param phoneNumber 인증 중인 전화번호
     * @param phoneCode 사용자가 입력한 인증 코드
     * @return 성공 시 갱신된 sessionId가 담긴 DTO, 실패 시 에러를 포함하는 Result 객체
     */
    suspend fun verifyPhoneCode(sessionId: String, phoneNumber: String, phoneCode: String): Result<SessionIdResponseDto>

    /**
     * 서버에 계좌 인증(1원 송금)을 요청합니다.
     */
    suspend fun requestAccountAuth(sessionId: String, email: String, bankAccount: String): Result<SessionIdResponseDto>

    /**
     * 서버에 계좌 1원 인증 코드의 유효성을 검증합니다.
     */
    suspend fun verifyAccountCode(sessionId: String, email: String, bankAccount: String, code: String): Result<SessionIdResponseDto>
    // TODO: 나중에 전화번호, 계좌, 최종 회원가입 등 다른 함수들을 여기에 추가해야 합니다.
}