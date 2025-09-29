package com.d108.moyeo.domain.usecase.user

import com.d108.moyeo.data.local.UserDataManager
import javax.inject.Inject

class SavePinUseCase @Inject constructor(
    private val userDataManager: UserDataManager
) {
    suspend operator fun invoke(pin: String): Result<Unit> {
        return runCatching {
            userDataManager.savePin(pin)
        }
    }
}
