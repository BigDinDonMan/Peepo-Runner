package com.peeporunner.util.timers

import com.peeporunner.util.Action
import kotlin.random.Random
import kotlin.random.nextInt

class RandomizedGameTimer(private val lowerBound: Int, private val upperBound: Int, looping: Boolean = false, elapsedAction: Action = {}) : AbstractTimer(looping, elapsedAction) {

    private var targetTime = Random.nextInt(lowerBound, upperBound)

    override fun update(deltaTime: Float) {
        if (!started) return
        elapsed += deltaTime
        if (elapsed >= targetTime) {
            elapsedAction.invoke()
            if (looping) {
                targetTime = Random.nextInt(lowerBound, upperBound)
                elapsed = 0f
            } else {
                stop()
            }
        }
    }
}