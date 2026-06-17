package com.toffeehpa.violline.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.toffeehpa.violline.core.model.loadMtu
import com.toffeehpa.violline.core.model.saveMtu
import com.toffeehpa.violline.ui.theme.ViollineTheme

@Composable
fun SettingsScreen() {
    val colors = ViollineTheme.colors
    val typography = ViollineTheme.typography
    val context = LocalContext.current
    var mtu by remember { mutableStateOf(loadMtu(context)) }
    var showMtuDialog by remember { mutableStateOf(false) }
    var mtuInput by remember { mutableStateOf(mtu.toString()) }

    if (showMtuDialog) {
        Dialog(onDismissRequest = { showMtuDialog = false }) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surface)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BasicText(
                    text = "Custom MTU",
                    style = TextStyle(
                        fontFamily = typography.fontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = colors.textPrimary
                    )
                )
                BasicTextField(
                    value = mtuInput,
                    onValueChange = { mtuInput = it.filter { c -> c.isDigit() } },
                    textStyle = TextStyle(
                        fontFamily = typography.fontFamily,
                        fontSize = 14.sp,
                        color = colors.textPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, colors.divider, RoundedCornerShape(6.dp))
                        .padding(12.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BasicText(
                        text = "Cancel",
                        style = TextStyle(
                            fontFamily = typography.fontFamily,
                            fontSize = 14.sp,
                            color = colors.textSecondary
                        ),
                        modifier = Modifier.clickable { showMtuDialog = false }
                    )
                    BasicText(
                        text = "Apply",
                        style = TextStyle(
                            fontFamily = typography.fontFamily,
                            fontSize = 14.sp,
                            color = colors.textPrimary
                        ),
                        modifier = Modifier.clickable {
                            val value = mtuInput.toIntOrNull()
                            if (value != null && value in 576..9000) {
                                mtu = value
                                saveMtu(context, value)
                            }
                            showMtuDialog = false
                        }
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 24.dp)
    ) {
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

        SettingsSection(title = "Network") {
            SettingsRow(label = "MTU") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1280, 1450, 1500).forEach { value ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .border(
                                    1.dp,
                                    if (mtu == value) colors.textPrimary else colors.divider,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    mtu = value
                                    saveMtu(context, value)
                                }
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
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .border(
                                1.dp,
                                if (mtu !in listOf(1280, 1450, 1500)) colors.textPrimary else colors.divider,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { showMtuDialog = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        BasicText(
                            text = if (mtu !in listOf(1280, 1450, 1500)) mtu.toString() else "...",
                            style = TextStyle(
                                fontFamily = typography.fontFamily,
                                fontSize = 13.sp,
                                color = if (mtu !in listOf(1280, 1450, 1500)) colors.textPrimary else colors.textSecondary
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(title = "Connection") {
            SettingsRow(label = "Kill Switch") {}
            SettingsDivider()
            SettingsRow(label = "Auto Reconnect") {}
            SettingsDivider()
            SettingsRow(label = "DNS") {}
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(title = "Advanced") {
            SettingsRow(label = "Split Tunneling") {}
            SettingsDivider()
            SettingsRow(label = "Logs") {}
            SettingsDivider()
            SettingsRow(label = "Diagnostics") {}
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(title = "About") {
            SettingsRow(label = "Version") {
                BasicText(
                    text = "0.1.0",
                    style = TextStyle(fontFamily = typography.fontFamily, fontSize = 13.sp, color = colors.textSecondary)
                )
            }
            SettingsDivider()
            SettingsRow(label = "Backend") {
                BasicText(
                    text = "sing-box",
                    style = TextStyle(fontFamily = typography.fontFamily, fontSize = 13.sp, color = colors.textSecondary)
                )
            }
            SettingsDivider()
            SettingsRow(label = "Licenses") {}
            SettingsDivider()
            SettingsRow(label = "Keep Android Open") {
                BasicText(
                    text = "↗",
                    style = TextStyle(fontFamily = typography.fontFamily, fontSize = 13.sp, color = colors.textSecondary),
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://keepandroidopen.org/")))
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsDivider() {
    val colors = ViollineTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(colors.divider)
    )
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
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
private fun SettingsRow(label: String, trailing: @Composable () -> Unit = {}) {
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
            style = TextStyle(fontFamily = typography.fontFamily, fontSize = 14.sp, color = colors.textPrimary)
        )
        trailing()
    }
}