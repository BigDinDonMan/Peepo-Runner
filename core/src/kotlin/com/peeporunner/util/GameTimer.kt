package com.peeporunner.util

class GameTimer(private val targetTime: Float, private val elapsedAction: Action, private val stepAction: Action = {}, var looping: Boolean = false) {
    private var elapsed = 0f
    private var started = false

    fun start() {
        started = true
        elapsed = 0f
    }

    fun update(delta: Float) {
        if (!started) return
        elapsed += delta
        stepAction.invoke()
        if (elapsed >= targetTime) {
            elapsedAction.invoke()
            if (looping) {
                elapsed = 0f
            } else {
                stop()
            }
        }
    }

    fun elapsedTime() = elapsed
    fun targetTime() = targetTime

    fun stop() {
        started = false
    }
}