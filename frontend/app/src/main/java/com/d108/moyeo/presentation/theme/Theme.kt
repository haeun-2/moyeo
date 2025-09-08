package com.d108.moyeo.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val lightScheme = lightColorScheme(
    // 주요 브랜드 색상 - 중요한 버튼, FAB, 액티브 상태에 사용
    primary = primaryLight,
    // primary 색상 위의 텍스트/아이콘 (primary 버튼 안의 텍스트)
    onPrimary = onPrimaryLight,
    // primary보다 연한 컨테이너 - 선택된 탭, 강조 카드 배경
    primaryContainer = primaryContainerLight,
    // primaryContainer 위의 텍스트/아이콘
    onPrimaryContainer = onPrimaryContainerLight,

    // 보조 액센트 색상 - 중간 강조도 요소, 필터 칩, 토글
    secondary = secondaryLight,
    // secondary 색상 위의 텍스트/아이콘
    onSecondary = onSecondaryLight,
    // secondary보다 연한 컨테이너
    secondaryContainer = secondaryContainerLight,
    // secondaryContainer 위의 텍스트/아이콘
    onSecondaryContainer = onSecondaryContainerLight,

    // 3차 액센트 색상 - 특별한 강조, 하이라이트
    tertiary = tertiaryLight,
    // tertiary 색상 위의 텍스트/아이콘
    onTertiary = onTertiaryLight,
    // tertiary보다 연한 컨테이너
    tertiaryContainer = tertiaryContainerLight,
    // tertiaryContainer 위의 텍스트/아이콘
    onTertiaryContainer = onTertiaryContainerLight,

    // 오류/위험 상황 색상 - 에러 메시지, 위험한 액션 버튼
    error = errorLight,
    // error 색상 위의 텍스트/아이콘
    onError = onErrorLight,
    // error보다 연한 컨테이너 - 에러 알림 배경
    errorContainer = errorContainerLight,
    // errorContainer 위의 텍스트/아이콘
    onErrorContainer = onErrorContainerLight,

    // 앱의 기본 배경 색상 - Scaffold 배경
    background = backgroundLight,
    // background 위의 텍스트/아이콘
    onBackground = onBackgroundLight,

    // 카드, 시트, 메뉴의 기본 표면 색상
    surface = surfaceLight,
    // surface 위의 기본 텍스트/아이콘
    onSurface = onSurfaceLight,
    // surface의 변형 - 구분이 필요한 다른 톤의 표면. 클릭되었거나 할 때
    surfaceVariant = surfaceVariantLight,
    // surfaceVariant 위의 텍스트 (보통 더 연한 텍스트)
    onSurfaceVariant = onSurfaceVariantLight,

    // 중요한 경계선, 포커스 링, TextField 테두리
    outline = outlineLight,
    // 덜 중요한 구분선, 장식적 경계선
    outlineVariant = outlineVariantLight,

    // 오버레이, 반투명 배경 (다이얼로그 뒤 어두운 배경)
    scrim = scrimLight,

    // 스낵바 같은 역방향 표면 (다크/라이트 테마 반대)
    inverseSurface = inverseSurfaceLight,
    // inverseSurface 위의 텍스트/아이콘
    inverseOnSurface = inverseOnSurfaceLight,
    // 역방향 컨텍스트에서의 primary 색상
    inversePrimary = inversePrimaryLight,

    // 어두운 표면 변형
    surfaceDim = surfaceDimLight,
    // 밝은 표면 변형
    surfaceBright = surfaceBrightLight,

    // 가장 낮은 elevation 표면 (거의 background와 비슷)
    surfaceContainerLowest = surfaceContainerLowestLight,
    // 낮은 elevation 표면
    surfaceContainerLow = surfaceContainerLowLight,
    // 기본 컨테이너 표면 (일반적인 카드)
    surfaceContainer = surfaceContainerLight,
    // 높은 elevation 표면 (떠있는 요소)
    surfaceContainerHigh = surfaceContainerHighLight,
    // 가장 높은 elevation 표면 (모달, 팝업)
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

@Composable
fun MoYeoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}