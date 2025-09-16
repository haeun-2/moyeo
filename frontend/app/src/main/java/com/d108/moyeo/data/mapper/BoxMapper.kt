package com.d108.moyeo.data.mapper

import com.d108.moyeo.data.remote.dto.box.BalanceDto
import com.d108.moyeo.data.remote.dto.box.BoxResponseDto
import com.d108.moyeo.domain.model.box.Balance
import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.model.box.BoxType

fun BoxResponseDto.toDomain() = Box(
    id = boxId,
    name = name,
    balances = balances.map(BalanceDto::toDomain),
    type = BoxType.from(type)
)

fun BalanceDto.toDomain() = Balance(
    currency = currency,
    balance = balance
)