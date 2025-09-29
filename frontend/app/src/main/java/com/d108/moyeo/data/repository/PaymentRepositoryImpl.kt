package com.d108.moyeo.data.repository

import com.d108.moyeo.data.remote.api.PaymentService
import com.d108.moyeo.domain.repository.PaymentRepository
import javax.inject.Inject

/**
 * PaymentRepository 인터페이스의 구현체 (실제 일꾼) 입니다.
 * @param paymentService Hilt를 통해 주입받은, 실제 네트워크 통신을 담당하는 Retrofit 서비스
 */
class PaymentRepositoryImpl @Inject constructor(
    private val paymentService: PaymentService
) : PaymentRepository {

    override suspend fun generateQRCode(boxId: Long): Result<String> {
        return runCatching {
            val response = paymentService.generateQRToken(boxId)

            if (response.isSuccessful && response.body() != null) {
                response.body()!!.token
            } else {
                throw Exception("Server responded with error: ${response.code()}")
            }
        }
    }
}