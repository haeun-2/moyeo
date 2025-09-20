package com.d108.moyeo.data.mapper

import com.d108.moyeo.data.remote.dto.history.HistoryTransactionDto
import com.d108.moyeo.data.remote.dto.history.PaginatedHistoryResponseDto
import com.d108.moyeo.domain.model.history.HistoryTransaction
import com.d108.moyeo.domain.model.history.PaginatedHistory

fun HistoryTransactionDto.toDomain(): HistoryTransaction = HistoryTransaction(
    id = this.historyId,
    datetime = this.datetime,
    title = this.title,
    amount = this.amount,
    balance = this.balance,
    currency = this.currency,
    memo = this.memo,
    category = this.category,
    type = this.transactionType
)

fun PaginatedHistoryResponseDto.toDomain(): PaginatedHistory = PaginatedHistory(
    page = this.page,
    hasNext = this.hasNext,
    content = this.content.map { it.toDomain() }
)