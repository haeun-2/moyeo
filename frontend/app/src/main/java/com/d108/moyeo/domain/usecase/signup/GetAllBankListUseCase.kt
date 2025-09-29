package com.d108.moyeo.domain.usecase.signup

import com.d108.moyeo.domain.model.Bank
import com.d108.moyeo.domain.repository.BankRepository
import jakarta.inject.Inject

class GetAllBankListUseCase @Inject constructor(
    private val bankRepository: BankRepository
) {


    suspend operator fun invoke(): Result<List<Bank>> {
        return bankRepository.getAllBankList()
    }
}

/*
Response<List<BankDto>>를 반환 → Retrofit이 HTTP 응답 코드, 헤더, 바디까지 감싸서 줌.
성공 시: response.isSuccessful == true, response.body()가 List<BankDto>
실패 시: response.isSuccessful == false, response.errorBody() 있음
 */
