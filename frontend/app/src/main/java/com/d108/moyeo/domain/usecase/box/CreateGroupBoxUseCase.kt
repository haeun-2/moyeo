package com.d108.moyeo.domain.usecase.box

import com.d108.moyeo.domain.repository.BoxRepository
import javax.inject.Inject

class CreateGroupBoxUseCase @Inject constructor(
    private val repository: BoxRepository
) {
    suspend operator fun invoke(name: String): Result<Long> = repository.createBox(name)
}
