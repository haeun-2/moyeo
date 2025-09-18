package com.d108.moyeo.domain.model.box

data class Balance(
    val currency: String,  // 화폐 코드(KRW, JPY, ...)
    val balance: Double  // 화폐량(소수점 넷째자리까지 들어옴)
)