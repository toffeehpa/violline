package com.toffeehpa.violline.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme

private val ViollineColorScheme = darkColorScheme(
    background = Color(0xFF0A0A0A),
    surface = Color(0xFF141414),
    primary = Color(0xFFFFFFFF),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
)

@Composable
fun ViollineTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ViollineColorScheme,
        content = content
    )
}