package com.peeporunner.ecs.components.physics

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.peeporunner.ecs.components.CollisionDataComponent
import com.peeporunner.ecs.components.mappers.CompMappers
import com.peeporunner.util.CollisionHandler

class CollisionListener : ContactListener {

    override fun beginContact(contact: Contact?) {
        processCollision(contact) { data -> data.onCollisionEnter }
    }

    override fun endContact(contact: Contact?) {
        processCollision(contact) { data -> data.onCollisionExit }
    }

    private fun processCollision(contact: Contact?, extractor: (CollisionDataComponent) -> CollisionHandler) {
        val entityA = contact!!.fixtureA.body.userData as Entity
        val entityB = contact.fixtureB.body.userData as Entity
        val collisionDataA = CompMappers.collisionDataMapper.get(entityA)
        val collisionDataB = CompMappers.collisionDataMapper.get(entityB)
        if (collisionDataA != null && collisionDataB != null) {
            extractor.invoke(collisionDataA).invoke(entityB)
            extractor.invoke(collisionDataB).invoke(entityA)
        }
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {}

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {}
}
