package com.d108.moyeo.data.repository

import com.d108.moyeo.data.remote.api.FcmTokenService
import com.d108.moyeo.data.remote.dto.fcm.FcmTokenRequestDto
import com.d108.moyeo.domain.repository.FcmTokenRepository
import javax.inject.Inject

class FcmTokenRepositoryImpl @Inject constructor(
    private val fcmTokenApiService: FcmTokenService
) : FcmTokenRepository {

    override suspend fun registerFcmToken(deviceToken: String): Result<Unit> {
        return try {
            val response = fcmTokenApiService.registerFcmToken(FcmTokenRequestDto(deviceToken))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to register FCM token with status: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}