package com.toffeehpa.violline.core.service

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import com.toffeehpa.violline.core.engine.XrayEngine

class ViollineVpnService : VpnService() {

    private val engine = XrayEngine()
    private var tunInterface: ParcelFileDescriptor? = null

    companion object {
        const val ACTION_START = "com.toffeehpa.violline.START"
        const val ACTION_STOP = "com.toffeehpa.violline.STOP"
        const val EXTRA_CONFIG = "config"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val config = intent.getStringExtra(EXTRA_CONFIG) ?: return START_NOT_STICKY
                startVpn(config)
            }
            ACTION_STOP -> stopVpn()
        }
        return START_STICKY
    }

    private fun startVpn(configJson: String) {
        tunInterface = Builder()
            .setSession("Violline")
            .addAddress("10.0.0.1", 24)
            .addDnsServer("1.1.1.1")
            .addRoute("0.0.0.0", 0)
            .establish()

        engine.start(configJson)
    }

    private fun stopVpn() {
        engine.stop()
        tunInterface?.close()
        tunInterface = null
        stopSelf()
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }
}