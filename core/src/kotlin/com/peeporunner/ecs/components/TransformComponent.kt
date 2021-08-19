package com.peeporunner.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool

class TransformComponent : Component, Pool.Poolable {
    val position = Vector3()
    val originalPosition = Vector3()
    val scale = Vector2(1f, 1f)
    val rotation = Vector2(0f, 0f)
    val size = Vector2(0f, 0f)

    override fun reset() {
        position.set(0f, 0f, 0f)
        scale.set(0f, 0f)
        rotation.set(0f, 0f)
        size.set(0f, 0f)
        originalPosition.set(0f, 0f, 0f)
    }
}
