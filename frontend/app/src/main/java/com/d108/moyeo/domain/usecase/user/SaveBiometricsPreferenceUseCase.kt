package com.d108.moyeo.domain.usecase.user

import com.d108.moyeo.data.local.UserDataManager
import javax.inject.Inject

class SaveBiometricsPreferenceUseCase @Inject constructor(
    private val userDataManager: UserDataManager
) {
    suspend operator fun invoke(isEnabled: Boolean): Result<Unit> {
        return runCatching {
            userDataManager.saveBiometricsPreference(isEnabled)
        }
    }
}
