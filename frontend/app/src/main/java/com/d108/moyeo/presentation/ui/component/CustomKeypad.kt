// CustomKeypad.kt
package com.d108.moyeo.presentation.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.d108.moyeo.presentation.theme.Typography
import com.d108.moyeo.presentation.theme.onSurfaceLight
import com.d108.moyeo.presentation.theme.primaryLight

/**
 * 키패드에서 발생하는 키 타입
 */
sealed interface KeypadKey {
    @JvmInline
    value class Digit(val value: Int) : KeypadKey {
        init { require(value in 0..9) }
        override fun toString(): String = value.toString()
    }

    data object Clear : KeypadKey { override fun toString(): String = "초기화" }
    data object Backspace : KeypadKey { override fun toString(): String = "삭제" }

    /**
     * 필요 시 임의 키를 추가하고 싶다면 사용 (예: "." 또는 "확인")
     */
    @Immutable
    data class Custom(val label: String, val tag: String = label) : KeypadKey {
        override fun toString(): String = label
    }
}

/**
 * 기본 3x4 PIN 레이아웃
 * 1 2 3
 * 4 5 6
 * 7 8 9
 * 초기화 0 ←
 */
@Immutable
data class KeypadLayout(
    val columns: Int = 3,
    val keys: List<KeypadKey> = listOf(
        KeypadKey.Digit(1), KeypadKey.Digit(2), KeypadKey.Digit(3),
        KeypadKey.Digit(4), KeypadKey.Digit(5), KeypadKey.Digit(6),
        KeypadKey.Digit(7), KeypadKey.Digit(8), KeypadKey.Digit(9),
        KeypadKey.Clear, KeypadKey.Digit(0), KeypadKey.Backspace
    )
)

/**
 * 색/테두리 등 스타일 디폴트
 */
object KeypadDefaults {
    @Composable
    fun digitButtonColors(): ButtonColors =
        ButtonDefaults.textButtonColors(
            contentColor = onSurfaceLight
        )

    @Composable
    fun functionButtonColors(): ButtonColors =
        ButtonDefaults.textButtonColors(
            contentColor = primaryLight
        )

    @Composable
    fun whiteButtonColors() : ButtonColors =
        ButtonDefaults.textButtonColors(
            contentColor = Color.White
        )
}

@Composable
fun CustomKeypad(
    modifier: Modifier = Modifier,
    layout: KeypadLayout = KeypadLayout(),
    /** 어떤 키가 눌렸는지 한 곳으로만 콜백 */
    onKeyPress: (KeypadKey) -> Unit,
    /** 각 버튼 모양 */
    buttonShape: Shape = CircleShape,
    /** 버튼 aspect 비율 (가로:세로) */
    buttonAspectRatio: Float = 1.2f,
    /** 버튼 내부 텍스트 스타일 */
    digitStyle: TextStyle = Typography.displayLarge,
    otherStyle: TextStyle = Typography.headlineMedium,
    keypadType: String
    ) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(layout.columns),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(layout.keys, key = { it.toString() }) { key ->
            val isDigit = key is KeypadKey.Digit
            val label = when (key) {
                is KeypadKey.Digit -> key.value.toString()
                KeypadKey.Clear -> "초기화"
                KeypadKey.Backspace -> "←"
                is KeypadKey.Custom -> key.label
            }
            val style = when (key) {
                KeypadKey.Clear, KeypadKey.Backspace -> otherStyle
                else -> digitStyle
            }

            TextButton(
                onClick = {
                    onKeyPress(key)
                },
                shape = buttonShape,
                colors = if (keypadType == "normal") { if (isDigit) KeypadDefaults.digitButtonColors() else KeypadDefaults.functionButtonColors() }
                        else { KeypadDefaults.whiteButtonColors() },
                border = null,
                modifier = Modifier
                    .aspectRatio(buttonAspectRatio)
                    .clip(buttonShape)
                    .semantics(mergeDescendants = true) {
                        contentDescription = when (key) {
                            is KeypadKey.Digit -> "숫자 ${key.value}"
                            KeypadKey.Clear -> "초기화"
                            KeypadKey.Backspace -> "삭제"
                            is KeypadKey.Custom -> key.label
                        }
                    }
                    .testTag("Keypad_${label}")
            ) {
                Text(text = label, style = style)
            }
        }
    }
}



/**
 * Preview
 */

@Composable
fun PinKeypadExample(
    modifier: Modifier = Modifier,
    onDigitClick: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    onBackspaceClick: () -> Unit = {}
) {
    CustomKeypad(
        modifier = modifier,
        onKeyPress = { key ->
            when (key) {
                is KeypadKey.Digit -> onDigitClick(key.value.toString())
                KeypadKey.Clear -> onClearClick()
                KeypadKey.Backspace -> onBackspaceClick()
                is KeypadKey.Custom -> { /* 필요 시 처리 */ }
            }
        },
        keypadType = "normal"
    )
}

@Composable
@Preview(showBackground = true, name = "Light")
private fun Preview_Keypad_Light() {
    MaterialTheme {
        PinKeypadExample()
    }
}

@Composable
@Preview(
    showBackground = true,
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
private fun Preview_Keypad_Dark() {
    MaterialTheme {
        PinKeypadExample()
    }
}
