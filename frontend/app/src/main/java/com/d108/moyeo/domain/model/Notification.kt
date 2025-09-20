package com.d108.moyeo.domain.model

data class Notification(
    val transactionId: Long,        // 거래 목록 id
    val title: String,              // 거래 제목

    // 거래 내용
    val time: String? = null,       // 거래 시각
    val sender: String? = null,     // 보낸 사람
    val amount: String? = null,     // 거래량
    val balance: String? = null,    // 거래 후 잔액

    val boxId: Long? = null,        // data.box 있으면 Long 으로
    val receivedAt: String,         // 날짜 문자열
)