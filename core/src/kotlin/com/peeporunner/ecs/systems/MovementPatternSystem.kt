package com.peeporunner.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.peeporunner.ecs.components.PhysicsBodyComponent
import com.peeporunner.ecs.components.TransformComponent
import com.peeporunner.ecs.components.mappers.CompMappers
import com.peeporunner.ecs.components.movement.MovementPatternComponent

class MovementPatternSystem : IteratingSystem(Family.all(TransformComponent::class.java, PhysicsBodyComponent::class.java, MovementPatternComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val pattern = CompMappers.movementPatternMapper.get(entity)
        val physicsBody = CompMappers.physicsBodyMapper.get(entity)
        val transform = CompMappers.transformMapper.get(entity)

        pattern.movementType.calculate(pattern, transform, physicsBody)//transform, physicsBody)
        pattern.time += deltaTime
    }
}