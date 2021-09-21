package com.peeporunner.ecs.components.movement

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.joints.MouseJoint
import com.badlogic.gdx.utils.Pool
import com.peeporunner.ecs.components.PhysicsBodyComponent
import com.peeporunner.ecs.components.TransformComponent
import kotlin.math.sin

class SineMovementComponent {
    var frequency = 0f
    var amplitude = 0f

//    override fun calculate(transform: TransformComponent, body: PhysicsBodyComponent) {
//        val originalY = originalPosition.y
//        val newY = originalY + sin(time * frequency) * amplitude
//        transform.position.y = newY
//        body.body!!.setTransform(body.body!!.position.x, newY + transform.size.y / 2 * transform.scale.y, body.body!!.angle)
//    }

//    override fun reset() {
//        frequency = 0f
//        amplitude = 0f
//        time = 0f
//    }
}
