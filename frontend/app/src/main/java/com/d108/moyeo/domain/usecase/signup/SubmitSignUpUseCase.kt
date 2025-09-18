package com.d108.moyeo.domain.usecase.signup

import com.d108.moyeo.domain.model.SignUpInfo
import com.d108.moyeo.domain.repository.SignUpRepository
import javax.inject.Inject

class SubmitSignUpUseCase @Inject constructor(
    private val signUpRepository: SignUpRepository
) {
    suspend operator fun invoke(sessionId: String, signUpInfo: SignUpInfo) =
        signUpRepository.signUp(sessionId, signUpInfo)
}