package com.peeporunner.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.peeporunner.ecs.components.PhysicsBodyComponent
import com.peeporunner.ecs.components.EnvironmentVelocityComponent
import com.peeporunner.ecs.components.mappers.CompMappers

class EnvironmentVelocitySystem : IteratingSystem(Family.all(PhysicsBodyComponent::class.java, EnvironmentVelocityComponent::class.java).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val physicsBodyComponent = CompMappers.physicsBodyMapper.get(entity)
        val velocityComponent = CompMappers.environmentVelocityMapper.get(entity)
        physicsBodyComponent?.body?.setLinearVelocity(velocityComponent.velocity.x, velocityComponent.velocity.y)
    }
}