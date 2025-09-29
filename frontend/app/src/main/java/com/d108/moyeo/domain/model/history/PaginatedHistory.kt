package com.d108.moyeo.domain.model.history

data class PaginatedHistory(
    val page: Int,
    val hasNext: Boolean,
    val content: List<HistoryTransaction>
)