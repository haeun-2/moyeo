package com.d108.moyeo.presentation.ui.screen.home.transfer

/**
 * 이체 화면의 상태(State)를 정의합니다.
 *
 * @property mode 현재 진행 중인 거래 종류 (이체, 입금)
 * @property currentStep 현재 진행 중인 이체 단계
 * @property currency 사용자가 선택한 보낼 화폐 (예: "KRW", "USD")
 * @property targetBox 사용자가 선택한 보낼 박스 ID
 * @property howMuch 사용자가 입력한 보낼 금액
 */
data class TransferUiState(
    val mode: TransferMode,
    val currentStep: TransferStep = TransferStep.TARGET_BOX,
    val currency: String = "",
    val targetBox: Long = -1,
    val howMuch: String = "",
    val pin: String = "",

    // PIN 검증을 위한 상태
    val pinFailureCount: Int = 0,
    val isPinLocked: Boolean = false,
    val pinError: String? = null,

    // 생체 인증 활성화 여부 확인
    val biometricsEnabled: Boolean = false
)

// 화면에 표시할 통화 데이터 클래스 (ViewModel에서 전달받을 데이터 모델)
data class CurrencyData(
    val name: String, // "대한민국 원"
    val code: String  // "KRW"
)
