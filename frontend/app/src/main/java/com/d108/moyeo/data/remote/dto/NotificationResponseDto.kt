package com.d108.moyeo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NotificationResponseDto(

    @SerializedName("transactionId")
    val transactionId: Long,          // 거래 목록 id

    @SerializedName("title")
    val title: String,                // 거래 제목 ([김코드의 박스] 이체)

    @SerializedName("body")
    val body: String,                 // 거래 내용 (날짜 /n 출발지 /n 잔액

    @SerializedName("data")
    val data: Map<String, String>?,   // ex) { "box": "2" }

    @SerializedName("receivedAt")
    val receivedAt: String            // ISO-like: "2025-09-19T13:35:55"
)