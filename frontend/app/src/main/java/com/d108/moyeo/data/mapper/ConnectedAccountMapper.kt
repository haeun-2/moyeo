package com.d108.moyeo.data.mapper

import com.d108.moyeo.data.remote.dto.account.ConnectedAccountResponseDto
import com.d108.moyeo.domain.model.ConnectedAccount

fun ConnectedAccountResponseDto.toDomain() = ConnectedAccount(
    bankName = bankName,
    bankLogoImg = bankLogoImg.orEmpty(),
    bankAccount = bankAccount
)