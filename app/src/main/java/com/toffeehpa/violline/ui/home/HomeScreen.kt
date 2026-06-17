package com.toffeehpa.violline.ui.home

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.net.VpnService
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.composables.core.Icon
import com.composables.icons.lucide.ClipboardPaste
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Power
import com.composables.icons.lucide.QrCode
import com.composables.icons.lucide.RefreshCw
import com.toffeehpa.violline.core.model.ConnectionState
import com.toffeehpa.violline.core.model.loadConfig
import com.toffeehpa.violline.core.model.loadMtu
import com.toffeehpa.violline.core.model.parseVlessUri
import com.toffeehpa.violline.core.model.saveConfig
import com.toffeehpa.violline.core.model.toSingBoxJson
import com.toffeehpa.violline.core.service.ViollineVpnService
import com.toffeehpa.violline.ui.theme.ViollineTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

private const val PREFS_NAME = "violline_prefs"
private const val KEY_WARN_SHOWN = "warn_shown"

@Composable
fun HomeScreen() {
    val colors = ViollineTheme.colors
    val typography = ViollineTheme.typography
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var connectionState by remember { mutableStateOf(ConnectionState.DISCONNECTED) }
    var currentConfig by remember { mutableStateOf<String?>(null) }
    var configName by remember { mutableStateOf<String?>(null) }
    var currentIp by remember { mutableStateOf<String?>(null) }
    var isIpLoading by remember { mutableStateOf(false) }
    var showWarnDialog by remember { mutableStateOf(false) }

    // Показываем диалог один раз
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_WARN_SHOWN, false)) {
            showWarnDialog = true
        }
        loadConfig(context)?.let { (json, name) ->
            currentConfig = json
            configName = name
        }
    }

    // Обновляем IP при подключении
    LaunchedEffect(connectionState) {
        if (connectionState == ConnectionState.CONNECTED) {
            isIpLoading = true
            currentIp = fetchIp()
            isIpLoading = false
        } else if (connectionState == ConnectionState.DISCONNECTED) {
            currentIp = null
        }
    }

    // Слушаем статус от сервиса
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action == ViollineVpnService.ACTION_STATUS) {
                    val connected = intent.getBooleanExtra(ViollineVpnService.EXTRA_CONNECTED, false)
                    connectionState = if (connected) ConnectionState.CONNECTED else ConnectionState.DISCONNECTED
                }
            }
        }
        val filter = IntentFilter(ViollineVpnService.ACTION_STATUS)
        context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        onDispose { context.unregisterReceiver(receiver) }
    }

    // Диалог предупреждения
    if (showWarnDialog) {
        Dialog(onDismissRequest = {}) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.surface)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BasicText(
                    text = "⚠️ Android под угрозой",
                    style = TextStyle(
                        fontFamily = typography.fontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = colors.textPrimary
                    )
                )
                BasicText(
                    text = "Google забирает контроль над вашим устройством. Узнайте, как это влияет на вашу свободу.",
                    style = TextStyle(
                        fontFamily = typography.fontFamily,
                        fontSize = 14.sp,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
                            .clickable {
                                showWarnDialog = false
                                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                                    .edit().putBoolean(KEY_WARN_SHOWN, true).apply()
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        BasicText(
                            text = "Закрыть",
                            style = TextStyle(
                                fontFamily = typography.fontFamily,
                                fontSize = 14.sp,
                                color = colors.textSecondary
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.textPrimary)
                            .clickable {
                                showWarnDialog = false
                                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                                    .edit().putBoolean(KEY_WARN_SHOWN, true).apply()
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://keepandroidopen.org/ru/"))
                                )
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        BasicText(
                            text = "Прочитать →",
                            style = TextStyle(
                                fontFamily = typography.fontFamily,
                                fontSize = 14.sp,
                                color = colors.background
                            )
                        )
                    }
                }
            }
        }
    }

    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentConfig?.let {
                startVpn(context, it)
                connectionState = ConnectionState.CONNECTING
            }
        }
    }

    fun toggleVpn() {
        when (connectionState) {
            ConnectionState.DISCONNECTED -> {
                val config = currentConfig
                if (config == null) {
                    Toast.makeText(context, "No configuration. Add from clipboard first.", Toast.LENGTH_SHORT).show()
                    return
                }
                val intent = VpnService.prepare(context)
                if (intent != null) {
                    vpnPermissionLauncher.launch(intent)
                } else {
                    startVpn(context, config)
                    connectionState = ConnectionState.CONNECTING
                }
            }
            ConnectionState.CONNECTING,
            ConnectionState.CONNECTED -> {
                stopVpn(context)
                connectionState = ConnectionState.DISCONNECTED
            }
        }
    }

    fun addFromClipboard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = clipboard.primaryClip?.getItemAt(0)?.text?.toString()?.trim()
        if (text == null || !text.startsWith("vless://")) {
            Toast.makeText(context, "No valid VLESS URI in clipboard", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val parsed = parseVlessUri(text)
            val mtu = loadMtu(context)
            currentConfig = parsed.toSingBoxJson(mtu = mtu)
            configName = parsed.name
            saveConfig(context, currentConfig!!, parsed.name)
            Toast.makeText(context, "Added: ${parsed.name}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to parse: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun copyConfig() {
        val config = currentConfig
        if (config == null) {
            Toast.makeText(context, "No configuration to copy", Toast.LENGTH_SHORT).show()
            return
        }
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText("Violline Config", config)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

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
                .padding(top = 56.dp, bottom = 24.dp),
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
                imageVector = Lucide.QrCode,
                contentDescription = "QR",
                tint = colors.textPrimary,
                modifier = Modifier.size(22.dp).clickable { }
            )
        }

        // IP блок
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BasicText(
                text = when {
                    isIpLoading -> "Loading..."
                    currentIp != null -> currentIp!!
                    else -> "—"
                },
                style = TextStyle(
                    fontFamily = typography.fontFamily,
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
            )
            if (connectionState == ConnectionState.CONNECTED) {
                Icon(
                    imageVector = Lucide.RefreshCw,
                    contentDescription = "Refresh IP",
                    tint = colors.textSecondary,
                    modifier = Modifier
                        .size(14.dp)
                        .clickable {
                            scope.launch {
                                isIpLoading = true
                                currentIp = fetchIp()
                                isIpLoading = false
                            }
                        }
                )
            }
        }

        // Статус
        BasicText(
            text = when (connectionState) {
                ConnectionState.DISCONNECTED -> if (configName != null) "Ready · $configName" else "Disconnected"
                ConnectionState.CONNECTING -> "Connecting..."
                ConnectionState.CONNECTED -> "Connected · $configName"
            },
            style = TextStyle(
                fontFamily = typography.fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = colors.textSecondary
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Кнопка VPN — по центру
        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .border(
                    width = 1.5.dp,
                    color = when (connectionState) {
                        ConnectionState.CONNECTED -> colors.textPrimary
                        else -> colors.textSecondary
                    },
                    shape = CircleShape
                )
                .clickable { toggleVpn() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Lucide.Power,
                contentDescription = "Toggle VPN",
                tint = when (connectionState) {
                    ConnectionState.CONNECTED -> colors.textPrimary
                    else -> colors.textSecondary
                },
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action кнопки
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
                    .clickable { addFromClipboard() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Lucide.ClipboardPaste,
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
                    .clickable { copyConfig() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Lucide.Copy,
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

private suspend fun fetchIp(): String? = withContext(Dispatchers.IO) {
    try {
        URL("https://api.ipify.org").readText().trim()
    } catch (e: Exception) {
        null
    }
}

private fun startVpn(context: Context, config: String) {
    val intent = Intent(context, ViollineVpnService::class.java).apply {
        action = ViollineVpnService.ACTION_START
        putExtra(ViollineVpnService.EXTRA_CONFIG, config)
    }
    context.startForegroundService(intent)
}

private fun stopVpn(context: Context) {
    val intent = Intent(context, ViollineVpnService::class.java).apply {
        action = ViollineVpnService.ACTION_STOP
    }
    context.startService(intent)
}