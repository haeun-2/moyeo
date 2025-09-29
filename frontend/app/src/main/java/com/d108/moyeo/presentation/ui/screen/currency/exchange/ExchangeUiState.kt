package com.d108.moyeo.presentation.ui.screen.currency.exchange

data class ExchangeUiState(
    val mode: ExchangeMode,
    val step: ExchangeStep = ExchangeStep.TARGET_BOX,

    // 박스/통화/금액
    val selectedBoxId: Long = -1L,
    val spendCurrencyCode: String = "",     // 지출 통화 코드 (예: "KRW", "USD")
    val spendCurrencyName: String = "",     // 지출 통화 이름 (UI 표시용)
    val targetCurrencyCode: String = "",  // 목표 통화 코드
    val targetCurrencyName: String = "",  // 목표 통화 이름

    // 사용자 입력값
    val amount: String = "0",                 // 입력 금액(문자열)

    // ViewModel 계산 결과 (UI 표시용) ---
    val requiredSpendAmount: Double = 0.0,    // 목표 금액을 위해 필요한 '지출 통화'의 양
    val availableSpendBalance: Double = 0.0,  // '지출 통화'의 보유 잔액

    // 환율 정보 (UI 표시용) ---
    val isMultiStepExchange: Boolean = false, // 이중환전 여부 (UI 분기 처리용)
    val rateForStep1: Double = 0.0,           // 1단계 환율 (단순환전 시에는 이것만 사용)
    val rateForStep2: Double = 0.0,            // 2단계 환율 (이중환전 시에만 사용)

    // --- ▼▼▼▼▼ 인증 관련 상태 추가 ▼▼▼▼▼ ---
    val pin: String = "",
    val pinError: String? = null,
    val pinFailureCount: Int = 0,
    val isPinLocked: Boolean = false,
    val biometricsEnabled: Boolean = false
)
