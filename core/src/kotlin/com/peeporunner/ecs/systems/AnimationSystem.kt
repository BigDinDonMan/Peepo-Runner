package com.peeporunner.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.peeporunner.ecs.components.AnimationComponent
import com.peeporunner.ecs.components.TextureComponent
import com.peeporunner.ecs.components.mappers.CompMappers

class AnimationSystem : IteratingSystem(Family.all(TextureComponent::class.java, AnimationComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val animationComponent = CompMappers.animationMapper.get(entity)
        val textureComponent = CompMappers.textureMapper.get(entity)

        textureComponent.textureRegion = animationComponent.currentFrame()
        if (animationComponent.isAnimationFinished()) {
            animationComponent.currentState().onAnimationFinished()
        } else {
            animationComponent.currentState().refreshCallback()
        }

        animationComponent.currentState().animTime += deltaTime
    }
}
