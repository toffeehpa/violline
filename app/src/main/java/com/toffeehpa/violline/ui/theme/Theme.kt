package com.toffeehpa.violline.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

data class ViollineColors(
    val background: Color,
    val surface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val divider: Color
)

data class ViollineTypography(
    val fontFamily: FontFamily
)

val LocalViollineColors = staticCompositionLocalOf {
    ViollineColors(
        background = Background,
        surface = Surface,
        textPrimary = TextPrimary,
        textSecondary = TextSecondary,
        accent = Accent,
        divider = Divider
    )
}

val LocalViollineTypography = staticCompositionLocalOf {
    ViollineTypography(fontFamily = FontFamily.Default)
}

object ViollineTheme {
    val colors: ViollineColors
        @Composable get() = LocalViollineColors.current
    val typography: ViollineTypography
        @Composable get() = LocalViollineTypography.current
}

@Composable
fun ViollineTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalViollineColors provides ViollineColors(
            background = Background,
            surface = Surface,
            textPrimary = TextPrimary,
            textSecondary = TextSecondary,
            accent = Accent,
            divider = Divider
        ),
        LocalViollineTypography provides ViollineTypography(
            fontFamily = IBMPlexSans
        ),
        content = content
    )
}