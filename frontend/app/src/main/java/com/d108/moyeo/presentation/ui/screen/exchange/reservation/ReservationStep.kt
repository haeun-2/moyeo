package com.d108.moyeo.presentation.ui.screen.exchange.reservation

enum class ReservationStep {
    Home,
    BoxSelection,       // 통장 선택
    CurrencySelection,  // 통화 선택
    RateInput,          // 목표 환율 입력
    AmountInput,        // 예약 금액 입력
    PeriodSelection,    // 기간 선택
    Finished       // 완료
}
