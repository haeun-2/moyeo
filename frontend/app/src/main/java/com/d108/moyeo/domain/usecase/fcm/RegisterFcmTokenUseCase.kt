package com.d108.moyeo.domain.usecase.fcm

import com.d108.moyeo.domain.repository.FcmTokenRepository
import javax.inject.Inject

class RegisterFcmTokenUseCase @Inject constructor(
    private val fcmTokenRepository: FcmTokenRepository
) {
    suspend operator fun invoke(deviceToken: String): Result<Unit> =
        fcmTokenRepository.registerFcmToken(deviceToken)
}
