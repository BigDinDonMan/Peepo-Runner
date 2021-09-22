package com.peeporunner.ecs.components.movement

import com.badlogic.gdx.math.Vector2
import com.peeporunner.ecs.components.PhysicsBodyComponent
import com.peeporunner.ecs.components.TransformComponent
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sinh

//this, this is beautiful
//I've been looking at this for 5 hours now
//but for real, different function implementation for each of the enum members is lit
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
            val radius = pattern.params["radius"]?.toFloat() ?: 1f
            val speed = pattern.params["speed"]?.toFloat() ?: 1f
            val arg = pattern.time * speed
            val newX = pattern.originalPosition.x + radius * cos(arg)
            val newY = pattern.originalPosition.y + radius * sin(arg)
            val diff = transform.position.x - newX
            pattern.originalPosition.x -= abs(diff)
            transform.position.x = newX
            transform.position.y = newY
            body.body!!.setTransform(newX + transform.size.x / 2 * transform.scale.x, newY + transform.size.y / 2 * transform.scale.y, body.body!!.angle)
        }
    };

    abstract fun calculate(pattern: MovementPatternComponent, transform: TransformComponent, body: PhysicsBodyComponent)
}