package com.peeporunner.ecs.components.movement

import com.badlogic.gdx.math.Vector2
import com.peeporunner.ecs.components.PhysicsBodyComponent
import com.peeporunner.ecs.components.TransformComponent
import kotlin.math.sin
import kotlin.math.sinh

enum class MovementType {
    SINE_WAVE {
        override fun calculate(pattern: MovementPatternComponent, transform: TransformComponent, body: PhysicsBodyComponent) {
            val originalY = pattern.originalPosition.y
            val frequency = pattern.params["frequency"]?.toFloat() ?: 1f
            val amplitude = pattern.params["amplitude"]?.toFloat() ?: 1f
            val newY = originalY + sin(pattern.time * frequency) * amplitude
            transform.position.y = newY
            body.body!!.setTransform(body.body!!.position.x, newY + transform.size.y / 2 * transform.scale.y, body.body!!.angle)
        }
    },
    PARAMETRIC_CIRCLE {
        override fun calculate(pattern: MovementPatternComponent, transform: TransformComponent, body: PhysicsBodyComponent) {

        }
    };

    abstract fun calculate(pattern: MovementPatternComponent, transform: TransformComponent, body: PhysicsBodyComponent)
}