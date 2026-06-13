package com.toffeehpa.violline.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.toffeehpa.violline.ui.theme.ViollineTheme

@Composable
fun SettingsScreen() {
    val colors = ViollineTheme.colors
    val typography = ViollineTheme.typography

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        BasicText(
            text = "Settings",
            style = TextStyle(
                fontFamily = typography.fontFamily,
                fontSize = 16.sp,
                color = colors.textSecondary
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}