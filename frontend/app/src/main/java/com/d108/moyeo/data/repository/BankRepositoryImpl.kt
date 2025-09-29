package com.d108.moyeo.data.repository

import com.d108.moyeo.data.mapper.toDomain
import com.d108.moyeo.data.remote.api.BankService
import com.d108.moyeo.domain.model.Bank
import com.d108.moyeo.domain.repository.BankRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * BankRepository 인터페이스의 구현체(실제 일꾼)입니다.
 * Hilt에게 이 클래스는 앱 전체에서 단 하나만 존재하는 싱글턴임을 알려줍니다.
 * @param bankService Hilt를 통해 주입받은, 실제 네트워크 통신을 담당하는 Retrofit 서비스
 */
@Singleton
class BankRepositoryImpl @Inject constructor(
    private val bankService: BankService
) : BankRepository {

    // 앱이 살아있는 동안 은행 목록을 한 번만 불러와 저장해두는 '캐시' 역할을 합니다.
    // 처음에는 비어있습니다 (null).
    private var cachedBankList: List<Bank>? = null

    /**
     * 서버 또는 캐시에서 전체 은행 목록을 가져옵니다.
     */
    override suspend fun getAllBankList(): Result<List<Bank>> {
        // 1. 먼저 캐시(메뉴판)가 비어있는지 확인합니다.
        if (cachedBankList != null) {
            // 캐시에 데이터가 있으면, 네트워크 통신 없이 즉시 성공 결과를 반환합니다.
            return Result.success(cachedBankList!!)
        }

        // 2. 캐시가 비어있으면, runCatching으로 서버 통신을 시도합니다.
        // runCatching은 네트워크 에러 등 예외가 발생해도 앱이 중단되지 않도록 안전하게 감싸줍니다.
        return runCatching {
            val response = bankService.getAllBanksList()

            if (!response.isSuccessful) {
                throw Exception("네트워크 에러: ${response.code()}")
            }

            val body = response.body() ?: emptyList()
            body.toDomain() // 여기서 List<Bank>로 변환됨
                .also { cachedBankList = it }
        }
    }
}


/*
여기서 핵심은 두 가지:
캐싱 → 서버 요청 최소화 (성능 + 비용 절감)
runCatching + Result →
서버 통신 성공 → Result.success(List<Bank>)
실패/예외 → Result.failure(Throwable)
이렇게 감싸서 UI 쪽에서 try-catch 없이 깔끔하게 분기 처리 가능.
 */

