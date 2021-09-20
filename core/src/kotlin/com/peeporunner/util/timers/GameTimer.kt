package com.peeporunner.util.timers

import com.peeporunner.util.Action

class GameTimer(private val targetTime: Float, elapsedAction: Action, private val stepAction: Action = {}, looping: Boolean = false) : AbstractTimer(looping, elapsedAction) {

    override fun update(deltaTime: Float) {
        if (!started) return
        elapsed += deltaTime
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

    fun targetTime() = targetTime
}