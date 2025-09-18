// domain/model/box/BoxDetail.kt
package com.d108.moyeo.domain.model.box

data class BoxDetail(
    val box: Box, // 기본 박스 정보
    val myPermission: Permission,
    val members: List<BoxMember>
)