package com.d108.moyeo.domain.usecase.box

import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.repository.BoxRepository
import javax.inject.Inject

class GetPersonalBoxUseCase @Inject constructor(
    private val repo: BoxRepository
) {
    suspend operator fun invoke(): Box = repo.getPersonalBox()
}