package com.toffeehpa.violline.core.engine

// import libviolline.Controller
// import libviolline.Libviolline

class XrayEngine : CoreEngine {
    // private val controller = Controller()

    override fun start(configJson: String, tunFd: Int): Result<Unit> {
        return Result.failure(Exception("XrayEngine disabled"))
    }

    override fun stop(): Result<Unit> = Result.success(Unit)

    override fun isRunning(): Boolean = false

    override fun version(): String = "xray (disabled)"
}