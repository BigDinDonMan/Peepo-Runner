package com.peeporunner.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

class EnvironmentVelocityComponent : Component, Pool.Poolable {
    val velocity = Vector2(0f, 0f)

    override fun reset() {
        velocity.set(0f, 0f)
    }
}