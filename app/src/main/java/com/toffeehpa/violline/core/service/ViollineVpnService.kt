package com.toffeehpa.violline.core.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.VpnService
import android.net.ConnectivityManager
import android.os.ParcelFileDescriptor
import android.util.Log
import com.toffeehpa.violline.core.engine.SingBoxEngine
import kotlinx.coroutines.*
import libcore.BoxInstance
import libcore.BoxPlatformInterface

private class SimpleDNSTransport : libcore.LocalDNSTransport {
    override fun raw(): Boolean = false
    override fun networkHandle(): Long = 0
    override fun lookup(ctx: libcore.ExchangeContext?, network: String?, domain: String?) {}
    override fun exchange(ctx: libcore.ExchangeContext?, message: ByteArray?) {}
}
class ViollineVpnService : VpnService(), BoxPlatformInterface {

    private val engine = SingBoxEngine()
    private var tunInterface: ParcelFileDescriptor? = null
    private var boxInstance: BoxInstance? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val TAG = "ViollineVPN"
        const val ACTION_START = "com.toffeehpa.violline.START"
        const val ACTION_STOP = "com.toffeehpa.violline.STOP"
        const val EXTRA_CONFIG = "config"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "violline_vpn"
    }

    // BoxPlatformInterface
    override fun autoDetectInterfaceControl(fd: Int) {
        protect(fd)
    }

    override fun openTun(singTunOptionsJson: String, tunPlatformOptionsJson: String): Long {
        Log.d(TAG, "openTun called: $singTunOptionsJson")
        tunInterface?.close()

        val options = org.json.JSONObject(singTunOptionsJson)
        val addresses = options.optJSONArray("address")

        val builder = Builder().setSession("Violline")

        if (addresses != null) {
            for (i in 0 until addresses.length()) {
                val cidr = addresses.getString(i)
                val parts = cidr.split("/")
                builder.addAddress(parts[0], parts[1].toInt())
            }
        } else {
            builder.addAddress("172.19.0.1", 30)
            builder.addAddress("fdfe:dcba:9876::1", 126)
        }

        builder.addDnsServer("1.1.1.1")
        builder.addRoute("0.0.0.0", 0)
        builder.addRoute("::", 0)
        builder.setMtu(options.optInt("mtu", 9000))
        builder.addDisallowedApplication(packageName)

        tunInterface = builder.establish()
        val fd = tunInterface?.fd?.toLong() ?: -1L
        Log.d(TAG, "TUN fd=$fd")
        return fd
    }

    override fun useProcFS(): Boolean = false

    override fun findConnectionOwner(
        ipProtocol: Int,
        sourceAddress: String,
        sourcePort: Int,
        destinationAddress: String,
        destinationPort: Int
    ): Int = -1

    override fun packageNameByUid(uid: Int): String = ""

    override fun uidByPackageName(packageName: String): Int = -1

    override fun wifiState(): String = ","

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand action=${intent?.action}")
        when (intent?.action) {
            ACTION_START -> {
                val config = intent.getStringExtra(EXTRA_CONFIG)
                if (config == null) {
                    Log.e(TAG, "Config is null, stopping")
                    return START_NOT_STICKY
                }
                startForeground(NOTIFICATION_ID, buildNotification())
                startVpn(config)
            }
            ACTION_STOP -> stopVpn()
        }
        return START_STICKY
    }

    private fun startVpn(configJson: String) {
        serviceScope.launch {
            try {
                Log.d(TAG, "Initializing libcore")
                val cm = getSystemService(ConnectivityManager::class.java)
                val underlyingNetwork = cm.activeNetwork

                val cacheDir = applicationContext.cacheDir.absolutePath + "/"
                val internalAssets = applicationContext.filesDir.absolutePath + "/"
                val externalAssets = applicationContext.getExternalFilesDir(null)?.absolutePath + "/"

                val nb4a = object : libcore.NB4AInterface {
                    override fun useOfficialAssets(): Boolean = false
                    override fun selector_OnProxySelected(selectorTag: String, tag: String) {}
                }

                libcore.Libcore.initCore(
                    "main",
                    cacheDir,
                    internalAssets,
                    externalAssets,
                    50,
                    true,
                    nb4a,
                    this@ViollineVpnService,
                    ViollineLocalDNSTransport(underlyingNetwork)
                )

                Log.d(TAG, "Creating sing-box instance")
                boxInstance = libcore.Libcore.newSingBoxInstance(configJson, null)
                boxInstance?.start()
                Log.d(TAG, "Sing-box started")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start: $e")
            }
        }
    }

    private fun stopVpn() {
        serviceScope.launch {
            Log.d(TAG, "Stopping sing-box")
            boxInstance?.close()
            boxInstance = null
            tunInterface?.close()
            tunInterface = null
            stopSelf()
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Violline VPN",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Violline")
            .setContentText("VPN is active")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        serviceScope.cancel()
        stopVpn()
        super.onDestroy()
    }
}