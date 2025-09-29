package com.d108.moyeo.domain.model.box

enum class BoxType { PERSONAL, GROUP;
    companion object {
        fun from(raw: String) = when (raw.uppercase()) {
            "PERSONAL" -> PERSONAL
            "GROUP" -> GROUP
            else -> GROUP // 안전 가드
        }
    }
}