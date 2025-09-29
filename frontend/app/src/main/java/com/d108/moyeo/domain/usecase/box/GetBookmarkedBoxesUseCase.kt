package com.d108.moyeo.domain.usecase.box

import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.repository.BoxRepository
import javax.inject.Inject

class GetBookmarkedBoxesUseCase @Inject constructor(
    private val boxRepository: BoxRepository
) {
    suspend operator fun invoke(): Result<List<Box>> {
        return boxRepository.getBookmarkedBoxes()
    }
}