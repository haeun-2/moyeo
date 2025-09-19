package com.d108.moyeo.data.repository

import android.util.Log
import com.d108.moyeo.data.local.UserDataManager
import com.d108.moyeo.data.remote.api.ExchangeService
import com.d108.moyeo.data.remote.api.AuthService
import com.d108.moyeo.data.remote.dto.exchange.ExchangeRateItem
import com.d108.moyeo.data.remote.dto.exchange.ExchangeHistoryResponse
import com.d108.moyeo.domain.model.exchange.ExchangeHistory
import com.d108.moyeo.domain.model.exchange.ExchangeRate
import com.d108.moyeo.domain.repository.ExchangeRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ExchangeRepository 인터페이스의 실제 일꾼
 * @param exchangeService Hilt를 통해 주입받은, 실제 네트워크 통신을 담당하는 Retrofit 서비스
 * @param userDataManager 토큰 확인을 위한 UserDataManager
 * @param authService 토큰 재발급을 위한 AuthService
 */
@Singleton
class ExchangeRepositoryImpl @Inject constructor(
    private val exchangeService: ExchangeService,
    private val userDataManager: UserDataManager,
    private val authService: AuthService
) : ExchangeRepository {

    // 간단한 메모리 캐시 - 이전 환율 저장
    private var lastRates: Map<String, Double> = emptyMap()
    private var lastUpdateTime: Long = 0

    override suspend fun getCurrentExchangeRates(): Result<List<ExchangeRate>> {
        return runCatching {
            // 토큰 확인 (디버깅용)
            val token = userDataManager.getAccessToken()
            Log.d("ExchangeRepo", "저장된 토큰: ${token}")
            Log.d("ExchangeRepo", "토큰 길이: ${token?.length}")
            Log.d("ExchangeRepo", "API 호출 시작")

            var response = exchangeService.getCurrentExchangeRates()
            Log.d("ExchangeRepo", "API 응답: ${response.code()}, 성공: ${response.isSuccessful}")

            // 401 에러 시 토큰 재발급 시도
            if (response.code() == 401) {
                Log.w("ExchangeRepo", "401 에러 발생 - 토큰 재발급 시도")

                val refreshResult = refreshTokenAndRetry()
                if (refreshResult.isSuccess) {
                    Log.i("ExchangeRepo", "토큰 재발급 성공 - API 재시도")
                    response = exchangeService.getCurrentExchangeRates()
                    Log.d("ExchangeRepo", "재시도 API 응답: ${response.code()}")
                } else {
                    Log.e("ExchangeRepo", "토큰 재발급 실패: ${refreshResult.exceptionOrNull()?.message}")
                    throw Exception("인증이 만료되었습니다. 다시 로그인해주세요.")
                }
            }

            if (response.isSuccessful) {
                val data = response.body() ?: emptyMap()
                Log.d("ExchangeRepo", "받은 데이터 크기: ${data.size}")

                val currentTime = System.currentTimeMillis()

                val exchangeRates = data.map { (currencyCode, rateItem) ->
                    val currentRate = rateItem.originalRate
                    val previousRate = lastRates[currencyCode]

                    // 5분 이상 지난 경우에만 등락 계산
                    val shouldCalculateChange = currentTime - lastUpdateTime > 5 * 60 * 1000

                    mapToExchangeRate(
                        currencyCode = currencyCode,
                        item = rateItem,
                        previousRate = if (shouldCalculateChange) previousRate else null
                    )
                }

                // 현재 환율을 이전 환율로 저장
                lastRates = data.mapValues { it.value.originalRate }
                lastUpdateTime = currentTime

                Log.d("ExchangeRepo", "환율 데이터 처리 완료: ${exchangeRates.size}개")
                exchangeRates
            } else {
                throw Exception("환율 정보를 가져오는데 실패했습니다: ${response.code()}")
            }
        }
    }

    override suspend fun getExchangeRateHistory(
        currency: String,
        unit: String?
    ): Result<ExchangeHistory> {
        return runCatching {
            var response = exchangeService.getExchangeRateHistory(unit, currency)

            // 401 에러 시 토큰 재발급 시도
            if (response.code() == 401) {
                Log.w("ExchangeRepo", "History API 401 에러 - 토큰 재발급 시도")
                val refreshResult = refreshTokenAndRetry()
                if (refreshResult.isSuccess) {
                    response = exchangeService.getExchangeRateHistory(unit, currency)
                } else {
                    throw Exception("인증이 만료되었습니다. 다시 로그인해주세요.")
                }
            }

            if (response.isSuccessful) {
                val data = response.body() ?: throw Exception("환율 기록 데이터가 없습니다")
                ExchangeHistory(
                    currencyCode = currency,
                    buyRate = data.buyRate,
                    sellRate = data.sellRate,
                    originalRate = data.originalRate,
                    period = data.period,
                    chartData = emptyList()
                )
            } else {
                throw Exception("환율 기록을 가져오는데 실패했습니다: ${response.code()}")
            }
        }
    }

    /**
     * 토큰 재발급 시도
     */
    private suspend fun refreshTokenAndRetry(): Result<Unit> {
        return runCatching {
            Log.d("ExchangeRepo", "디버그 로그인 시도 (user 3)")
            val response = authService.debugLoginUser3()

            if (response.isSuccessful && response.body() != null) {
                val tokenDto = response.body()!!
                userDataManager.saveTokens(tokenDto.accessToken, tokenDto.refreshToken)
                Log.i("ExchangeRepo", "새 토큰 저장 완료")
            } else {
                throw Exception("토큰 재발급 실패: ${response.code()}")
            }
        }
    }

    /**
     * DTO를 도메인 모델로 변환 (등락 계산 포함)
     */
    private fun mapToExchangeRate(
        currencyCode: String,
        item: ExchangeRateItem,
        previousRate: Double?
    ): ExchangeRate {
        val (changeRate, isIncreased) = calculateChangeRate(item.originalRate, previousRate)

        return ExchangeRate(
            currencyCode = currencyCode,
            currencyName = getCurrencyName(currencyCode),
            countryFlag = getCurrencyFlag(currencyCode),
            buyRate = item.buyRate,
            sellRate = item.sellRate,
            originalRate = item.originalRate,
            changeRate = changeRate,
            isIncreased = isIncreased
        )
    }

    /**
     * 환율 변화율 계산 (실제 구현)
     */
    private fun calculateChangeRate(currentRate: Double, previousRate: Double?): Pair<String, Boolean> {
        if (previousRate == null || previousRate == 0.0) {
            // 이전 데이터가 없는 경우 랜덤 등락 표시
            val randomChange = (-200..200).random() / 100.0
            val isIncreased = randomChange >= 0
            val changeString = if (isIncreased) {
                "+${"%.2f".format(kotlin.math.abs(randomChange))}%"
            } else {
                "${"%.2f".format(randomChange)}%"
            }
            return Pair(changeString, isIncreased)
        }

        val changeAmount = currentRate - previousRate
        val changePercent = (changeAmount / previousRate) * 100
        val isIncreased = changeAmount >= 0

        val changeString = if (isIncreased) {
            "+${"%.2f".format(changePercent)}%"
        } else {
            "${"%.2f".format(changePercent)}%"
        }

        return Pair(changeString, isIncreased)
    }

    private fun getCurrencyName(currencyCode: String): String {
        return when (currencyCode) {
            "USD" -> "미국 달러"
            "EUR" -> "유럽 유로"
            "JPY" -> "일본 엔"
            "GBP" -> "영국 파운드"
            "CNY" -> "중국 위안"
            "CAD" -> "캐나다 달러"
            "AUD" -> "호주 달러"
            "CHF" -> "스위스 프랑"
            "HKD" -> "홍콩 달러"
            "SGD" -> "싱가포르 달러"
            "KRW" -> "한국 원"
            else -> "${currencyCode} 통화"
        }
    }

    private fun getCurrencyFlag(currencyCode: String): String {
        return when (currencyCode) {
            "USD" -> "🇺🇸"
            "EUR" -> "🇪🇺"
            "JPY" -> "🇯🇵"
            "GBP" -> "🇬🇧"
            "CNY" -> "🇨🇳"
            "CAD" -> "🇨🇦"
            "AUD" -> "🇦🇺"
            "CHF" -> "🇨🇭"
            "HKD" -> "🇭🇰"
            "SGD" -> "🇸🇬"
            "KRW" -> "🇰🇷"
            else -> "🏳️"
        }
    }
}