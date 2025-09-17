package com.d108.moyeo.domain.usecase.box

import com.d108.moyeo.domain.repository.BoxRepository
import javax.inject.Inject

class AddBookmarkUseCase @Inject constructor(
    private val boxRepository: BoxRepository
) {
    suspend operator fun invoke(boxId: Long): Result<Unit> {
        return boxRepository.addBookmark(boxId)
    }
}