package com.d108.moyeo.domain.model.box

data class InviteLinkResult(
    val inviteLink: String,    // 초대 링크
    val inviteCode: String,    // 초대 코드
    val expiresAt: String      // 만료 일자
)
