package com.toffeehpa.violline.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.composables.icons.lucide.QrCode
import com.composables.core.Icon
import com.composables.icons.lucide.ClipboardCopy
import com.composables.icons.lucide.ClipboardPlus
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Power
import com.composables.icons.lucide.ScanQrCode
import com.toffeehpa.violline.core.model.ConnectionState
import com.toffeehpa.violline.ui.theme.ViollineTheme

@Composable
fun HomeScreen() {
    val colors = ViollineTheme.colors
    val typography = ViollineTheme.typography
    var connectionState by remember { mutableStateOf(ConnectionState.DISCONNECTED) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 24.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicText(
                text = "Violline",
                style = TextStyle(
                    fontFamily = typography.fontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = colors.textPrimary
                )
            )
            Icon(
                imageVector = Lucide.ScanQrCode,
                contentDescription = "QR",
                tint = colors.textPrimary,
                modifier = Modifier
                    .size(22.dp)
                    .clickable { }
            )
        }

        // Status
        BasicText(
            text = when (connectionState) {
                ConnectionState.DISCONNECTED -> "Disconnected"
                ConnectionState.CONNECTING -> "Connecting..."
                ConnectionState.CONNECTED -> "Connected"
            },
            style = TextStyle(
                fontFamily = typography.fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = colors.textSecondary
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Power Button
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .border(
                    width = 1.5.dp,
                    color = when (connectionState) {
                        ConnectionState.DISCONNECTED -> colors.textSecondary
                        ConnectionState.CONNECTING -> colors.textSecondary
                        ConnectionState.CONNECTED -> colors.textPrimary
                    },
                    shape = CircleShape
                )
                .clickable {
                    connectionState = when (connectionState) {
                        ConnectionState.DISCONNECTED -> ConnectionState.CONNECTING
                        ConnectionState.CONNECTING -> ConnectionState.CONNECTED
                        ConnectionState.CONNECTED -> ConnectionState.DISCONNECTED
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Lucide.Power,
                contentDescription = "Toggle VPN",
                tint = when (connectionState) {
                    ConnectionState.DISCONNECTED -> colors.textSecondary
                    ConnectionState.CONNECTING -> colors.textSecondary
                    ConnectionState.CONNECTED -> colors.textPrimary
                },
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
                    .clickable { }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Lucide.ClipboardPlus,
                    contentDescription = null,
                    tint = colors.textPrimary,
                    modifier = Modifier.size(18.dp)
                )
                BasicText(
                    text = "Add from clipboard",
                    style = TextStyle(
                        fontFamily = typography.fontFamily,
                        fontSize = 14.sp,
                        color = colors.textPrimary
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
                    .clickable { }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Lucide.ClipboardCopy,
                    contentDescription = null,
                    tint = colors.textPrimary,
                    modifier = Modifier.size(18.dp)
                )
                BasicText(
                    text = "Copy configuration",
                    style = TextStyle(
                        fontFamily = typography.fontFamily,
                        fontSize = 14.sp,
                        color = colors.textPrimary
                    )
                )
            }
        }
    }
}