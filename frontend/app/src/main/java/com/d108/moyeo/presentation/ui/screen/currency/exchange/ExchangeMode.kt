package com.d108.moyeo.presentation.ui.screen.currency.exchange

enum class ExchangeMode {
    CHARGE,   // 충전: 박스 선택 → 지출통화 선택 → 금액 입력 → 완료
    REFUND    // 돌려받기: 내 박스 고정 + (지출통화=선택 외화, toCurrency=KRW) → 금액 입력 → 완료
}