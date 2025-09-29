package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.BankDto
import retrofit2.Response
import retrofit2.http.GET

/**
 * 은행(Bank) 관련 API 명세를 정의하는 Retrofit 서비스 인터페이스입니다.
 */
interface BankService {

    /**
     * 서버로부터 전체 은행 정보 목록을 조회합니다.
     * @return 성공 시 BankDto 객체들의 리스트
     */
    @GET("api/banks/info")
    suspend fun getAllBanksList(): Response<List<BankDto>>
}

/*
Response<List<BankDto>>를 반환 → Retrofit이 HTTP 응답 코드, 헤더, 바디까지 감싸서 줌.

성공 시: response.isSuccessful == true, response.body()가 List<BankDto>

실패 시: response.isSuccessful == false, response.errorBody() 있음
 */