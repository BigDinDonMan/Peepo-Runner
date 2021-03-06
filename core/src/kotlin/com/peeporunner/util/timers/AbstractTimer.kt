package com.peeporunner.util.timers

import com.peeporunner.util.Action

abstract class AbstractTimer(val looping: Boolean = false, val elapsedAction: Action = {}) {
    protected var elapsed = 0f
    protected var started = false

    fun start() {
        started = true
        elapsed = 0f
    }

    fun stop() {
        started = false
    }

    //difference between this and start is that it doesnt reset elapsed time
    fun resume() {
        started = true
    }

    fun elapsedTime() = elapsed

    abstract fun update(deltaTime: Float)
}