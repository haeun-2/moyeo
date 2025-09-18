package com.d108.moyeo.domain.usecase.box

import com.d108.moyeo.domain.model.box.BoxDetail
import com.d108.moyeo.domain.repository.BoxRepository
import javax.inject.Inject

class GetBoxDetailUseCase @Inject constructor(
    private val boxRepository: BoxRepository
) {
    suspend operator fun invoke(boxId: Long): Result<BoxDetail> {
        return boxRepository.getBoxDetail(boxId)
    }
}