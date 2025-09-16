package com.d108.moyeo.domain.model

/**
 * 최종 회원가입 시, ViewModel에서 UseCase와 Repository로 전달할
 * 모든 정보를 담는 순수한 데이터 모델
 */
data class SignUpInfo(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val bankCode: String,
    val accountNumber: String,
    val fid: String // Firebase ID 등
)