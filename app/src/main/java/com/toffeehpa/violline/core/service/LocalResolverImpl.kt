package com.toffeehpa.violline.core.service

import android.net.ConnectivityManager
import android.net.DnsResolver
import android.net.Network
import android.os.Build
import android.os.CancellationSignal
import android.system.ErrnoException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import libcore.ExchangeContext
import libcore.LocalDNSTransport
import java.net.InetAddress
import java.net.UnknownHostException

class ViollineLocalDNSTransport(private val network: Network?) : LocalDNSTransport {

    override fun raw(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    override fun networkHandle(): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            network?.networkHandle ?: 0L
        } else 0L
    }

    override fun exchange(ctx: ExchangeContext, message: ByteArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val signal = CancellationSignal()
            ctx.onCancel(signal::cancel)
            DnsResolver.getInstance().rawQuery(
                network, message, DnsResolver.FLAG_NO_RETRY,
                Dispatchers.IO.asExecutor(), signal,
                object : DnsResolver.Callback<ByteArray> {
                    override fun onAnswer(answer: ByteArray, rcode: Int) { ctx.rawSuccess(answer) }
                    override fun onError(error: DnsResolver.DnsException) {
                        val cause = error.cause
                        if (cause is ErrnoException) ctx.errnoCode(cause.errno)
                        else ctx.errnoCode(114514)
                    }
                }
            )
        } else {
            ctx.errnoCode(114514)
        }
    }

    override fun lookup(ctx: ExchangeContext, network: String, domain: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val signal = CancellationSignal()
            ctx.onCancel(signal::cancel)
            val type = when {
                network.endsWith("4") -> DnsResolver.TYPE_A
                network.endsWith("6") -> DnsResolver.TYPE_AAAA
                else -> null
            }
            val callback = object : DnsResolver.Callback<Collection<InetAddress>> {
                override fun onAnswer(answer: Collection<InetAddress>, rcode: Int) {
                    if (rcode == 0) ctx.success(answer.mapNotNull { it.hostAddress }.joinToString("\n"))
                    else ctx.errorCode(rcode)
                }
                override fun onError(error: DnsResolver.DnsException) {
                    val cause = error.cause
                    if (cause is ErrnoException) ctx.errnoCode(cause.errno)
                    else ctx.errnoCode(114514)
                }
            }
            if (type != null) {
                DnsResolver.getInstance().query(this.network, domain, type, DnsResolver.FLAG_NO_RETRY, Dispatchers.IO.asExecutor(), signal, callback)
            } else {
                DnsResolver.getInstance().query(this.network, domain, DnsResolver.FLAG_NO_RETRY, Dispatchers.IO.asExecutor(), signal, callback)
            }
        } else {
            try {
                val answer = InetAddress.getAllByName(domain)
                ctx.success(answer.mapNotNull { it.hostAddress }.joinToString("\n"))
            } catch (e: UnknownHostException) {
                ctx.errorCode(3)
            }
        }
    }
}