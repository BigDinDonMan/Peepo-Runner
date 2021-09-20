package com.peeporunner.util.timers

import com.peeporunner.util.Action
import kotlin.random.Random
import kotlin.random.nextInt

class RandomizedGameTimer(private val lowerBound: Double, private val upperBound: Double, looping: Boolean = false, elapsedAction: Action = {}) : AbstractTimer(looping, elapsedAction) {

    private var targetTime = 0.0

    init {
        targetTime = Random.nextDouble(lowerBound, upperBound)
    }

    override fun update(deltaTime: Float) {
        if (!started) return
        elapsed += deltaTime
        if (elapsed >= targetTime) {
            elapsedAction.invoke()
            if (looping) {
                targetTime = Random.nextDouble(lowerBound, upperBound)
                elapsed = 0f
            } else {
                stop()
            }
        }
    }
}