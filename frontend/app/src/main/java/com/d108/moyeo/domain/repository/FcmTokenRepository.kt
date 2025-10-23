package com.d108.moyeo.domain.repository

interface FcmTokenRepository {
    suspend fun registerFcmToken(deviceToken: String): Result<Unit>
}