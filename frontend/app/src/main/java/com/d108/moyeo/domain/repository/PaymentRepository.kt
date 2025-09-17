package com.d108.moyeo.domain.repository

interface PaymentRepository {
    /**
     * 특정 모여박스에 대한 결제용 QR 토큰 발급을 서버에 요청합니다.
     * @param boxId QR 코드를 생성할 박스의 ID
     * @return 성공 시 토큰 문자열, 실패 시 에러를 포함하는 Result 객체
     */
    suspend fun generateQRCode(boxId: Long): Result<String>
}