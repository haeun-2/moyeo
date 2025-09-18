// domain/model/box/BoxMember.kt
package com.d108.moyeo.domain.model.box

data class BoxMember(
    val id: Long,
    val name: String,
    val permission: Permission // 권한 정보를 Permission 객체로 가짐
)