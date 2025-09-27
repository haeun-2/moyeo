package com.d108.moyeo.presentation.ui.screen.currency.exchange

data class ExchangeUiState(
    val mode: ExchangeMode,
    val step: ExchangeStep = ExchangeStep.TARGET_BOX,

    // 박스/통화/금액
    val selectedBoxId: Long = -1L,
    val spendCurrencyCode: String = "",     // 지출 통화 코드 (예: "KRW", "USD")
    val spendCurrencyName: String = "",     // 지출 통화 이름 (UI 표시용)
    val amount: String = "0",                 // 입력 금액(문자열)

    val pin: String = "",
    val pinFailureCount: Int = 0,
    val isPinLocked: Boolean = false,
    val pinError: String? = null,
    val biometricsEnabled: Boolean = false
)
