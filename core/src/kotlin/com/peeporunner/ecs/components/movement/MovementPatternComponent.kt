package com.peeporunner.ecs.components.movement

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.peeporunner.ecs.components.PhysicsBodyComponent
import com.peeporunner.ecs.components.TransformComponent

class MovementPatternComponent : Component, Pool.Poolable {
    val originalPosition = Vector2()
    val params = HashMap<String, Number>()
    var time = 0f
    lateinit var movementType: MovementType

    override fun reset() {
        time = 0f
        params.clear()
        originalPosition.set(0f, 0f)
    }
}