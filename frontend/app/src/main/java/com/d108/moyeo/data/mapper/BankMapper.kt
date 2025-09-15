package com.d108.moyeo.data.mapper


import com.d108.moyeo.data.remote.dto.BankDto
import com.d108.moyeo.domain.model.Bank
import com.d108.moyeo.util.base64ToImageBitmap

// BankDto 객체 하나를 Bank 모델로 변환
fun BankDto.toDomain(): Bank {
    return Bank(
        code = this.bankCode,
        name = this.bankName,
        logoBitmap = this.encodedBankLogoImg?.let { base64ToImageBitmap(it) }
    )
}

// BankDto 리스트 전체를 Bank 모델 리스트로 변환합니다.
fun List<BankDto>.toDomain(): List<Bank> {
    return this.map { it.toDomain() }
}

/*
fun Response<List<BankDto>>.toDomain(): List<Bank> {
    if (!isSuccessful) throw Exception("네트워크 에러: $code()")
    return body()?.map { it.toDomain() } ?: emptyList()
}
여기서 Response<List<BankDto>> → List<Bank> 변환

실패하면 Exception 터트림 → runCatching이 잡아줌

성공하면 DTO → Domain Model(Bank) 변환
 */