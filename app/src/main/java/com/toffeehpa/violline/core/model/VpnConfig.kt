package com.toffeehpa.violline.core.model

import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject

data class VlessConfig(
    val uuid: String,
    val host: String,
    val port: Int,
    val security: String,
    val sni: String,
    val fingerprint: String,
    val publicKey: String,
    val shortId: String,
    val network: String,
    val flow: String,
    val name: String
)

fun parseVlessUri(uri: String): VlessConfig {
    val parsed = Uri.parse(uri)
    val uuid = parsed.userInfo ?: error("No UUID")
    val host = parsed.host ?: error("No host")
    val port = parsed.port.takeIf { it > 0 } ?: error("No port")
    return VlessConfig(
        uuid = uuid,
        host = host,
        port = port,
        security = parsed.getQueryParameter("security") ?: "none",
        sni = parsed.getQueryParameter("sni") ?: host,
        fingerprint = parsed.getQueryParameter("fp") ?: "chrome",
        publicKey = parsed.getQueryParameter("pbk") ?: "",
        shortId = parsed.getQueryParameter("sid") ?: "",
        network = parsed.getQueryParameter("type") ?: "tcp",
        flow = parsed.getQueryParameter("flow") ?: "",
        name = parsed.fragment ?: host
    )
}

fun VlessConfig.toSingBoxJson(mtu: Int = 1500): String {
    return JSONObject().apply {
        put("log", JSONObject().put("level", "warn").put("timestamp", true))
        put("dns", JSONObject().apply {
            put("servers", JSONArray().apply {
                put(JSONObject().apply {
                    put("tag", "remote")
                    put("address", "tls://1.1.1.1")
                    put("detour", "proxy")
                })
                put(JSONObject().apply {
                    put("tag", "local")
                    put("address", "local")
                    put("detour", "direct")
                })
            })
            put("rules", JSONArray().apply {
                put(JSONObject().apply {
                    put("outbound", "any")
                    put("server", "local")
                })
            })
            put("final", "remote")
        })
        put("inbounds", JSONArray().apply {
            put(JSONObject().apply {
                put("type", "tun")
                put("tag", "tun-in")
                put("address", JSONArray().apply {
                    put("172.19.0.1/30")
                    put("fdfe:dcba:9876::1/126")
                })
                put("mtu", mtu)
                put("auto_route", true)
                put("strict_route", true)
                put("stack", "gvisor")
                put("sniff", true)
            })
        })
        put("outbounds", JSONArray().apply {
            put(JSONObject().apply {
                put("type", "vless")
                put("tag", "proxy")
                put("server", host)
                put("server_port", port)
                put("uuid", uuid)
                put("flow", flow)
                put("tls", JSONObject().apply {
                    put("enabled", true)
                    put("server_name", sni)
                    put("reality", JSONObject().apply {
                        put("enabled", true)
                        put("public_key", publicKey)
                        put("short_id", shortId)
                    })
                    put("utls", JSONObject().apply {
                        put("enabled", true)
                        put("fingerprint", fingerprint)
                    })
                })
            })
            put(JSONObject().apply { put("type", "direct"); put("tag", "direct") })
            put(JSONObject().apply { put("type", "block"); put("tag", "block") })
            put(JSONObject().apply { put("type", "dns"); put("tag", "dns-out") })
        })
        put("route", JSONObject().apply {
            put("rules", JSONArray().apply {
                put(JSONObject().apply { put("protocol", "dns"); put("outbound", "dns-out") })
                put(JSONObject().apply { put("ip_is_private", true); put("outbound", "direct") })
            })
            put("final", "proxy")
            put("auto_detect_interface", true)
        })
    }.toString()
}