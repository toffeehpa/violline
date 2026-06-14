package com.toffeehpa.violline.core.engine

interface CoreEngine {
    fun start(configJson: String): Result<Unit>
    fun stop(): Result<Unit>
    fun isRunning(): Boolean
    fun version(): String
}