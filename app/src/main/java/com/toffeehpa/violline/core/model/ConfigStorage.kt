package com.toffeehpa.violline.core.model

import android.content.Context

private const val PREFS_NAME = "violline_prefs"
private const val KEY_CONFIG_JSON = "config_json"
private const val KEY_CONFIG_NAME = "config_name"

fun saveConfig(context: Context, configJson: String, name: String) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        .putString(KEY_CONFIG_JSON, configJson)
        .putString(KEY_CONFIG_NAME, name)
        .apply()
}

fun loadConfig(context: Context): Pair<String, String>? {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val json = prefs.getString(KEY_CONFIG_JSON, null) ?: return null
    val name = prefs.getString(KEY_CONFIG_NAME, "Unknown") ?: "Unknown"
    return Pair(json, name)
}