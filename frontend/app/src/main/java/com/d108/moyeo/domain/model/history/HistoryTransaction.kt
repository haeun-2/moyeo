package com.d108.moyeo.domain.model.history

data class HistoryTransaction(
    val id: Long,
    val datetime: String,
    val title: String,
    val amount: Double,
    val balance: Double,
    val currency: String,
    val memo: String?,
    val category: String,
    val type: String
)
