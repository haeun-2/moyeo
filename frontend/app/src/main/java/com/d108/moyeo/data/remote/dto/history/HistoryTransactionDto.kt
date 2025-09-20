package com.d108.moyeo.data.remote.dto.history

import com.google.gson.annotations.SerializedName

data class HistoryTransactionDto(
    // 거래내역 하나하나의 정보를 담는 DTO
    @SerializedName("historyId") val historyId: Long,
    @SerializedName("datetime") val datetime: String,
    @SerializedName("title") val title: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("balance") val balance: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("memo") val memo: String?,
    @SerializedName("category") val category: String,
    @SerializedName("transactionType") val transactionType: String

)
