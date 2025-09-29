package com.d108.moyeo.presentation.ui.screen.home

/** 홈 화면 → 다른 화면으로의 단방향 네비 이벤트 */
sealed class HomeNavigationEvent {
    data class NavigateToMyWallet(val boxId: Long, val currencyCode: String) : HomeNavigationEvent()

    data class NavigateToMyBox(val boxId: Long) : HomeNavigationEvent()
    data class NavigateToTransfer(val currencyId: String) : HomeNavigationEvent()
    // 기본 통화는 KRW
    data class NavigateToDeposit(val boxId: Long, val currencyId: String = "KRW"): HomeNavigationEvent()
}
