package com.d108.moyeo.data.mapper

import com.d108.moyeo.data.remote.dto.box.BalanceDto
import com.d108.moyeo.data.remote.dto.box.GroupBoxResponseDto
import com.d108.moyeo.data.remote.dto.box.PersonalBoxResponseDto
import com.d108.moyeo.domain.model.box.Balance
import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.model.box.BoxType

fun PersonalBoxResponseDto.toDomain() = Box(
    id = boxId,
    name = name,
    balances = balances.map(BalanceDto::toDomain),
    type = BoxType.from(type),
    isBookmarked = false  // 개인 박스는 언제나 false
)

fun GroupBoxResponseDto.toDomain() = Box(
    id = boxId,
    name = name,
    balances = balances.map(BalanceDto::toDomain),
    type = BoxType.from(type),
    isBookmarked = isBookmarked // 모여 박스는 서버 값을 받음
)

fun BalanceDto.toDomain() = Balance(
    currency = currency,
    balance = balance
)