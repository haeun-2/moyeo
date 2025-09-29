package com.d108.moyeo.domain.model

import androidx.compose.ui.graphics.ImageBitmap

data class Bank(  // 서버에서 받은 은행 정보를 UI에서 활용하기 좋게 변경
    val code: String,
    val name: String,
    val logoBitmap: ImageBitmap? //디코딩된 이미지를 담을 속성 추가 (실패할 수 있으므로 Nullable)
)
