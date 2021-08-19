package com.peeporunner.util

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Joint
import com.badlogic.gdx.physics.box2d.World

class EntityRemovalService(private val engine: Engine, private val entityPool: EntityPool, private val world: World) {

    private val entities = ArrayList<Entity>()
    private val bodies = ArrayList<Body>()
    private val joints = ArrayList<Joint>()

    fun process() {
        processMarkedEntities()
        destroyMarkedBodies()
        destroyMarkedJoints()
    }

    private fun processMarkedEntities() {
        entities.forEach { e -> run {
            engine.removeEntity(e)
            entityPool.free(e)
        } }
        entities.clear()
    }

    private fun destroyMarkedBodies() {
        bodies.forEach { body -> world.destroyBody(body) }
        bodies.clear()
    }

    private fun destroyMarkedJoints() {
        joints.forEach { joint -> world.destroyJoint(joint) }
        joints.clear()
    }

    fun mark(e: Entity) {
        entities += e
    }

    fun mark(b: Body) {
        bodies += b
    }

    fun mark(j: Joint) {
        joints += j
    }

    fun markMultipleEntities(collection: Collection<Entity>) {
        entities += collection
    }

    fun markMultipleBodies(collection: Collection<Body>) {
        bodies += collection
    }

    fun markMultipleJoints(collection: Collection<Joint>) {
        joints += collection
    }
}