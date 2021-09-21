package com.peeporunner.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family

import com.badlogic.ashley.systems.IteratingSystem
import com.peeporunner.ecs.components.PhysicsBodyComponent
import com.peeporunner.ecs.components.movement.SineMovementComponent
import com.peeporunner.ecs.components.TransformComponent
import com.peeporunner.ecs.components.mappers.CompMappers

import kotlin.math.sin

class SineMovementSystem : IteratingSystem(Family.all(TransformComponent::class.java, PhysicsBodyComponent::class.java).get()) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
//        val originalY = transformComponent.originalPosition.y
//        val newY = originalY + sin(sineMovementComponent.time * sineMovementComponent.frequency) * sineMovementComponent.amplitude
//        transformComponent.position.y = newY
//        bodyComponent.body!!.setTransform(bodyComponent.body!!.position.x, newY + transformComponent.size.y / 2 * transformComponent.scale.y, bodyComponent.body!!.angle)
//        sineMovementComponent.time += deltaTime
    }
}
