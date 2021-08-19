package com.peeporunner.util

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.World
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class DeferredEntityInitializationService(private val engine: Engine) {

    private val generalInitializerQueue = ArrayList<Pair<Entity, (Entity) -> Entity>>()

    private val accessLock = ReentrantLock()

    // function for general (non-physics) entities
    fun queueEntity(entity: Entity, initializer: (Entity) -> Entity) {
        accessLock.withLock { generalInitializerQueue += Pair(entity, initializer) }
    }

    // function for deferred initialization of physics-based entities
    fun process() {
        accessLock.withLock {
            generalInitializerQueue.forEach { (e, init) -> kotlin.run {
                init.invoke(e)
                engine.addEntity(e)
            }}
            generalInitializerQueue.clear()
        }
    }
}