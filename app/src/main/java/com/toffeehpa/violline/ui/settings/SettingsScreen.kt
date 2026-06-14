package com.toffeehpa.violline.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toffeehpa.violline.ui.theme.ViollineTheme

@Composable
fun SettingsScreen() {
    val colors = ViollineTheme.colors
    val typography = ViollineTheme.typography
    var mtu by remember { mutableStateOf(1500) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 24.dp)
    ) {
        // Top Bar
        BasicText(
            text = "Settings",
            style = TextStyle(
                fontFamily = typography.fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = colors.textPrimary
            ),
            modifier = Modifier.padding(top = 56.dp, bottom = 32.dp)
        )

        // MTU
        SettingsSection(title = "Network") {
            SettingsRow(label = "MTU") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1280, 1400, 1500).forEach { value ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .border(
                                    1.dp,
                                    if (mtu == value) colors.textPrimary else colors.divider,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { mtu = value }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            BasicText(
                                text = value.toString(),
                                style = TextStyle(
                                    fontFamily = typography.fontFamily,
                                    fontSize = 13.sp,
                                    color = if (mtu == value) colors.textPrimary else colors.textSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Placeholders
        SettingsSection(title = "Connection") {
            SettingsRow(label = "Kill Switch") {}
            SettingsRow(label = "Auto Reconnect") {}
            SettingsRow(label = "DNS") {}
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(title = "Advanced") {
            SettingsRow(label = "Split Tunneling") {}
            SettingsRow(label = "Logs") {}
            SettingsRow(label = "Diagnostics") {}
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(title = "About") {
            SettingsRow(label = "Version") {
                BasicText(
                    text = "0.1.0",
                    style = TextStyle(
                        fontFamily = typography.fontFamily,
                        fontSize = 13.sp,
                        color = colors.textSecondary
                    )
                )
            }
            SettingsRow(label = "Backend") {
                BasicText(
                    text = "xray-core",
                    style = TextStyle(
                        fontFamily = typography.fontFamily,
                        fontSize = 13.sp,
                        color = colors.textSecondary
                    )
                )
            }
            SettingsRow(label = "Licenses") {}
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = ViollineTheme.colors
    val typography = ViollineTheme.typography

    Column {
        BasicText(
            text = title.uppercase(),
            style = TextStyle(
                fontFamily = typography.fontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = colors.textSecondary,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, colors.divider, RoundedCornerShape(10.dp))
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsRow(
    label: String,
    trailing: @Composable () -> Unit = {}
) {
    val colors = ViollineTheme.colors
    val typography = ViollineTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicText(
            text = label,
            style = TextStyle(
                fontFamily = typography.fontFamily,
                fontSize = 14.sp,
                color = colors.textPrimary
            )
        )
        trailing()
    }
}