package com.d108.moyeo.domain.model.box

data class UpdatePermissionRequest(
    val boxMemberId: Long,
    val canTransfer: Boolean,
    val canPayment: Boolean,
    val canExchange: Boolean
)
