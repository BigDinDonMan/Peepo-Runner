package com.peeporunner.ecs.components.mappers

import com.badlogic.ashley.core.ComponentMapper
import com.peeporunner.ecs.components.*
import com.peeporunner.ecs.components.gamelogic.EnemyComponent
import com.peeporunner.ecs.components.gamelogic.PeepoPlayerComponent
import com.peeporunner.ecs.components.movement.MovementPatternComponent

object CompMappers {
    val transformMapper: ComponentMapper<TransformComponent> = ComponentMapper.getFor(TransformComponent::class.java)
    val animationMapper: ComponentMapper<AnimationComponent> = ComponentMapper.getFor(AnimationComponent::class.java)
    val textureMapper: ComponentMapper<TextureComponent> = ComponentMapper.getFor(TextureComponent::class.java)
    val tagMapper: ComponentMapper<TagComponent> = ComponentMapper.getFor(TagComponent::class.java)
    val physicsBodyMapper: ComponentMapper<PhysicsBodyComponent> = ComponentMapper.getFor(PhysicsBodyComponent::class.java)
    val audioMapper: ComponentMapper<AudioComponent> = ComponentMapper.getFor(AudioComponent::class.java)
    val collisionDataMapper: ComponentMapper<CollisionDataComponent> = ComponentMapper.getFor(CollisionDataComponent::class.java)
    val playerComponentMapper: ComponentMapper<PeepoPlayerComponent> = ComponentMapper.getFor(PeepoPlayerComponent::class.java)
    val environmentVelocityMapper: ComponentMapper<EnvironmentVelocityComponent> = ComponentMapper.getFor(EnvironmentVelocityComponent::class.java)
    val movementPatternMapper: ComponentMapper<MovementPatternComponent> = ComponentMapper.getFor(MovementPatternComponent::class.java)
    val enemyMapper: ComponentMapper<EnemyComponent> = ComponentMapper.getFor(EnemyComponent::class.java)
}
