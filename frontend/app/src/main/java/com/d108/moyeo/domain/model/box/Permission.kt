
package com.d108.moyeo.domain.model.box

data class Permission(
    val canTransfer: Boolean,
    val canPayment: Boolean,
    val canExchange: Boolean,
    val isOwner: Boolean
)