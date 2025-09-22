package com.d108.moyeo.domain.usecase.box

import com.d108.moyeo.domain.repository.BoxRepository
import javax.inject.Inject

class JoinBoxUseCase @Inject constructor(
    private val repository: BoxRepository
) {
    suspend operator fun invoke(code: String): Result<Long> =
        repository.joinBox(code)
}