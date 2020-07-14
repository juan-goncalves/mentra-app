package me.juangoncalves.mentra.ui.common

import androidx.compose.Composable
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.graphics.Color
import androidx.ui.material.*
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.unit.sp

@Composable
fun MentraApp(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    MentraTheme(darkTheme = darkTheme) {
        Surface {
            content()
        }
    }
}

private val LightColorPalette = lightColorPalette(
    primary = Color(0x87, 0x76, 0xE5),
    primaryVariant = Color(0xFF, 0x36, 0x81),
    onPrimary = Color(0xFF, 0xFF, 0xFF),
    onSurface = Color.DarkGray
)

private val DarkColorPalette = darkColorPalette(
    primary = Color(0x87, 0x76, 0xE5),
    primaryVariant = Color(0xFF, 0x36, 0x81),
    surface = Color(0x34, 0x3E, 0x5C),
    onPrimary = Color(0xFF, 0xFF, 0xFF),
    onSurface = Color(0xFF, 0xFF, 0xFF)
)

@Composable
private val subtitle1: TextStyle
    get() = MaterialTheme.typography.subtitle1.copy(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )

@Composable
private val subtitle2: TextStyle
    get() = MaterialTheme.typography.subtitle2.copy(letterSpacing = 3.sp)

@Composable
private fun captionFor(palette: ColorPalette): TextStyle = MaterialTheme.typography.caption.copy(
    color = palette.onSurface.copy(alpha = 0.8f),
    fontSize = 14.sp
)

@Composable
fun MentraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colorPalette = if (darkTheme) DarkColorPalette else LightColorPalette
    MaterialTheme(
        colors = colorPalette,
        typography = MaterialTheme.typography.copy(
            subtitle1 = subtitle1,
            subtitle2 = subtitle2,
            caption = captionFor(colorPalette)
        )
    ) {
        content()
    }
}