package com.d108.moyeo.domain.usecase.box

import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.repository.BoxRepository
import javax.inject.Inject

class GetPaymentBoxesUseCase @Inject constructor(
    private val repository: BoxRepository
) {
    suspend operator fun invoke(): Result<List<Box>> =
        repository.getPaymentBoxes()
}