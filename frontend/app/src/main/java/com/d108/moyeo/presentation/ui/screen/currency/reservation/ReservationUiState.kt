package com.d108.moyeo.presentation.ui.screen.currency.reservation

import com.d108.moyeo.domain.model.exchange.Reservation

data class ReservationUiState(
    val step: ReservationStep = ReservationStep.Home,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val entry: ReservationEntry = ReservationEntry.HOME,

    // (a) 박스
    val selectedBoxId: Long = -1L,

    // (b) 예약 화폐(입금 통화)
    val toCurrencyCode: String = "",
    val toCurrencyName: String = "",

    // (c) 목표 환율 (문자열 입력)
    val targetRate: String = "0",
    // 현재 환율(표시용) — 없으면 빈 문자열로 전달
    val currentRate: String = "",

    // (금액) — AmountInputContent 시그니처를 맞추기 위한 필드
    // 탭: "KRW" 또는 toCurrencyCode
    val selectedTab: String = "KRW",
    // 현재 탭 기준 입력값(문자열)
    val inputAmount: String = "0",
    // 환산값(표시용)
    val krwValue: Double = 0.0,
    val foreignValue: Double = 0.0,

    // (d) 기간
    val periodStart: String = "",
    val periodEnd: String = "",

    // (선택) 표시용
    val myKrwBalanceText: String = "",

    val reservations: List<Reservation> = emptyList(),

    val pin: String = "",
    val pinFailureCount: Int = 0,
    val isPinLocked: Boolean = false,
    val pinError: String? = null,
    val biometricsEnabled: Boolean = false
)

