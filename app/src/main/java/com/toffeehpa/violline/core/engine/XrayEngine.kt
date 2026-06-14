package com.toffeehpa.violline.core.engine

import libviolline.Controller
import libviolline.Libviolline

class XrayEngine : CoreEngine {
    private val controller = Controller()

    override fun start(configJson: String): Result<Unit> {
        return try {
            controller.start(configJson)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun stop(): Result<Unit> {
        return try {
            controller.stop()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isRunning(): Boolean = controller.isRunning

    override fun version(): String = Libviolline.version()
}