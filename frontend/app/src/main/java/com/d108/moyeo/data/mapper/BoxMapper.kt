package com.d108.moyeo.data.mapper

import com.d108.moyeo.data.remote.dto.box.BalanceDto
import com.d108.moyeo.data.remote.dto.box.BoxDetailResponseDto
import com.d108.moyeo.data.remote.dto.box.BoxMemberDto
import com.d108.moyeo.data.remote.dto.box.GroupBoxResponseDto
import com.d108.moyeo.data.remote.dto.box.PermissionDto
import com.d108.moyeo.data.remote.dto.box.PersonalBoxResponseDto
import com.d108.moyeo.domain.model.box.Balance
import com.d108.moyeo.domain.model.box.Box
import com.d108.moyeo.domain.model.box.BoxDetail
import com.d108.moyeo.domain.model.box.BoxMember
import com.d108.moyeo.domain.model.box.BoxType
import com.d108.moyeo.domain.model.box.Permission

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

fun BoxDetailResponseDto.toDomain(): BoxDetail {
    // DTO를 Box, Permission, List<BoxMember> 세 부분으로 분해하여 조립합니다.
    return BoxDetail(
        box = Box(
            id = this.boxId,
            name = this.name,
            balances = this.balances.map { it.toDomain() },
            type = BoxType.from(this.type)
        ),
        myPermission = this.permission.toDomain(),
        members = this.members.map { it.toDomain() }
    )
}

fun PermissionDto.toDomain(): Permission {
    return Permission(
        canTransfer = this.canTransfer,
        canPayment = this.canPayment,
        canExchange = this.canExchange,
        isOwner = this.isOwner
    )
}

fun BoxMemberDto.toDomain(): BoxMember {
    val permission = Permission(
        canTransfer = this.canTransfer,
        canPayment = this.canPayment,
        canExchange = this.canExchange,
        isOwner = this.isOwner
    )
    return BoxMember(
        id = this.boxMemberId,
        name = this.name,
        permission = permission
    )
}