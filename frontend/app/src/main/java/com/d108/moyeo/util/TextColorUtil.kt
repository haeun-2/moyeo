package com.d108.moyeo.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * 배경색에 따라 가독성 좋은 글자색(흰색/검은색)을 반환.
 */
fun textColorUtil(background: Color): Color {  // Box의 백그라운드 컬러를 넣으면 글자색을 흰검 중 하나로 지정해줌
    return if (background.luminance() > 0.4) {
        Color.Black
    } else {
        Color.White
    }
}
