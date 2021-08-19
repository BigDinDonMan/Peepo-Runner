package com.peeporunner.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.peeporunner.ecs.components.AudioComponent

class AudioSystem : IteratingSystem(Family.all(AudioComponent::class.java).get()) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {

    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
    }
}
