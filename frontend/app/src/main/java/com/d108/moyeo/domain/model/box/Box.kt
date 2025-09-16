package com.d108.moyeo.domain.model.box

data class Box(
    val id: Long,
    val name: String,
    val balances: List<Balance>,
    val type: BoxType
)