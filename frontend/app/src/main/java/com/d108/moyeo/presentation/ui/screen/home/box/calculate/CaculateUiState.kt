package com.d108.moyeo.presentation.ui.screen.home.box.calculate

import com.d108.moyeo.core.BoxStoreUiState
import com.d108.moyeo.domain.model.box.BoxMember
import com.d108.moyeo.presentation.ui.component.home.Currency

data class CalculateUiState(
    val currentStep: CalculateStep = CalculateStep.CHOOSE_CURRENCY,
    val boxInfo: BoxStoreUiState? = null,
    val participants: List<BoxMember> = emptyList(),  // 초기 원본 멤버

    // 통화 선택
    val availableCurrencies: List<Currency> = emptyList(), // 정산 가능한 통화 목록 (잔액 > 0)
    val selectedCurrencies: Set<String> = emptySet(),      // '다중 선택'된 통화 코드

    // '단계별' 정산을 위한 상태 ---
    val settlementQueue: List<String> = emptyList(), // 정산할 통화 코드 목록 (예: ["KRW", "JPY"])
    val currentSettlementIndex: Int = 0,             // 현재 정산 중인 통화의 인덱스

    // 현재에 대한 정산 정보
    val totalSettlementAmount: Long = 0L,  // 정수형으로 변환
    val settlementParticipants: List<SettlementParticipant> = emptyList(), // 정산 받을 멤버
    val selectedMemberIds: Set<Long> = emptySet(),    // 선택된 멤버 ID

    val isSettlementSumValid: Boolean = false,

    val pin: String = "",
    // PIN 검증을 위한 상태
    val pinFailureCount: Int = 0,
    val isPinLocked: Boolean = false,
    val pinError: String? = null,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,


    // API 호출
    val allSettlementDetails: Map<String, List<SettlementParticipant>> = emptyMap(),
    val isSubmitting: Boolean = false
)

data class SettlementParticipant(
    val member: BoxMember,
    val amount: Long = 0L,
    val amountStr: String = "0",       // UI TextField에 표시될 문자열
    val isManuallyEdited: Boolean = false,  // 사용자가 직접 수정했는지 여부
    val isEnabled: Boolean = true,  // 체크박스에서 체크 여부
)