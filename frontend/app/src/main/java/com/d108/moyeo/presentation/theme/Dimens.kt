package com.d108.moyeo.presentation.theme

import androidx.compose.ui.unit.dp

/**
 * 컴포넌트 내부 또는 컴포넌트 사이의 작은 간격 (Spacer, contentPadding 등)에 사용합니다.
 * 이름으로 크기를 유추할 수 있는 일반적인 간격 값입니다.
 */

object Spacing {
    val ExtraSmall = 4.dp
    val Small = 8.dp
    val SmallMedium = 12.dp
    val Medium = 16.dp
    val Large = 24.dp
    val ExtraLarge = 32.dp
}

/**
 * 화면 전체의 구조를 잡는 패딩
 * 이름에 용도가 명확히 드러나는 구체적인 간격 값입니다.
 */
object Padding {
    // 수평 (좌우) 패딩

    val HorizontalExtraSmall = 16.dp
    val HorizontalSmall = 24.dp
    val HorizontalMedium = 32.dp
    val HorizontalLarge = 40.dp

    // 수직 (상하) 패딩
    val VerticalExtraSmall = 16.dp
    val VerticalSmall = 24.dp
    val VerticalMedium = 32.dp
    val VerticalLarge = 40.dp

    // 화면 상하 여백
    val ScreenTop = 48.dp
    val ScreenTopLarge = 72.dp

    val ScreenBottomSmall = 32.dp
    val ScreenBottom = 48.dp
    val ScreenBottomLarge = 72.dp

    // 일반적인 컨텐츠 영역의 기본 패딩
    val Content = 16.dp
}