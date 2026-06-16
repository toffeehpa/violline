package com.toffeehpa.violline.core.engine

import libcore.Libcore

class SingBoxEngine {
    private var instance: libcore.BoxInstance? = null

    fun start(configJson: String): Result<Unit> {
        return try {
            instance = Libcore.newSingBoxInstance(configJson, null)
            instance?.start()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun stop(): Result<Unit> {
        return try {
            instance?.close()
            instance = null
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isRunning(): Boolean = instance != null

    fun version(): String = Libcore.versionBox()
}