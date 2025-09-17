package com.d108.moyeo.domain.usecase.payment

import com.d108.moyeo.domain.repository.PaymentRepository
import javax.inject.Inject


class GenerateQRTokenUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(boxId: Long): Result<String> {
        return paymentRepository.generateQRCode(boxId)
    }
}