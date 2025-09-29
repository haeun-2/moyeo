package com.d108.moyeo.domain.repository

import com.d108.moyeo.data.remote.dto.auth.MeDto
import com.d108.moyeo.domain.model.Token
import kotlinx.coroutines.flow.Flow

/**
 * 인증(로그인, 로그아웃 등) 기능과 관련된 데이터 처리를 위한 인터페이스 (설계도).
 */
interface AuthRepository {

    val accessToken: Flow<String?>
    val refreshToken: Flow<String?>

    /**
     * [디버그용] 유저 3번으로 로그인하여 토큰을 받아 저장합니다.
     * @return 성공 시 Token 객체, 실패 시 에러를 포함하는 Result 객체
     */
    suspend fun debugLoginUser3(): Result<Token>


    /**
     * [디버그용] 유저 4번으로 로그인하여 토큰을 받아 저장합니다.
     * @return 성공 시 Token 객체, 실패 시 에러를 포함하는 Result 객체
     */
    suspend fun debugLoginUser4(): Result<Token>

    /**
     * 실제 로그인을 요청하고, 성공 시 토큰을 저장합니다.
     * @param phoneNumber 사용자의 전화번호
     * @param fid Firebase ID 등 고유 식별자
     * @return 성공 시 Token 객체, 실패 시 에러를 포함하는 Result 객체
     */
    suspend fun login(phoneNumber: String, fid: String): Result<Token>

    suspend fun getMe(): Result<MeDto>

    // TODO: 나중에 로그아웃, 토큰 재발급 등의 함수를 여기에 추가해야 합니다.

}