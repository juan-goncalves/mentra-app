package me.juangoncalves.mentra.core.presentation

import androidx.compose.Composable
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette

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
    onPrimary = Color(0xFF, 0xFF, 0xFF)
)

private val DarkColorPalette = darkColorPalette(
    primary = Color(0x87, 0x76, 0xE5),
    primaryVariant = Color(0xFF, 0x36, 0x81),
    surface = Color(0x34, 0x3E, 0x5C),
    onPrimary = Color(0xFF, 0xFF, 0xFF)
)

@Composable
fun MentraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colorPalette = if (darkTheme) DarkColorPalette else LightColorPalette
    MaterialTheme(
        colors = colorPalette
    ) {
        content()
    }
}